package com.google.teampot.service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.mail.MessagingException;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventAttendee;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.appengine.api.taskqueue.TaskOptions.Method;
import com.google.teampot.GoogleServices;
import com.google.teampot.api.API;
import com.google.teampot.dao.MeetingDAO;
import com.google.teampot.dao.ProjectDAO;
import com.google.teampot.diff.visitor.EntityDiffVisitor;
import com.google.teampot.model.EntityDiff;
import com.google.teampot.model.Meeting;
import com.google.teampot.model.MeetingActivityEvent;
import com.google.teampot.model.MeetingActivityEventVerb;
import com.google.teampot.model.MeetingPollProposedDate;
import com.google.teampot.model.MeetingPollVote;
import com.google.teampot.model.Project;
import com.google.teampot.model.TaskActivityEventVerb;
import com.google.teampot.model.User;
import com.google.teampot.util.AppHelper;

import de.danielbechler.diff.ObjectDifferBuilder;
import de.danielbechler.diff.node.DiffNode;

public class MeetingService{

	private static MeetingService instance;
	
	private MeetingDAO dao;
	private ProjectDAO projectDAO;
	private static ActivityEventService activityEventService = ActivityEventService.getInstance();
	
	private MeetingService() {
		this.dao = new MeetingDAO();
		this.projectDAO = new ProjectDAO();
	}
	
	public static MeetingService getInstance() {
		if (instance == null) instance = new MeetingService();
		return instance;
	}
	
	public List<Meeting> list(String projectId) {
		return dao.list(projectId);
	}
	
	public List<Meeting> list() {
		return dao.list();
	}
	
	public Meeting get(String key){
		return dao.get(key);
	}
	
	public void save(Meeting entity){
		this.save(entity,null);
	}
	
	public void save(Meeting entity, User actor){
		MeetingActivityEvent activtyEvent = new MeetingActivityEvent();
		
		MeetingActivityEventVerb verb = this.getSaveActivityVerb(entity,actor);
		activtyEvent.setVerb(verb);
		
		Meeting oldEntity = null;
		if (verb != MeetingActivityEventVerb.CREATE) oldEntity = dao.get(entity.getKey());	
		
		// first save
		dao.save(entity);
		
		// send notifications
		try {
			switch (verb) {
			case SCHEDULE:
				this.sendMeetingScheduledNotification(entity, actor);
				break;
			case POLL:
				this.sendMeetingPollNotification(entity,actor);
				break;
			default:
				break;
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		if (verb == MeetingActivityEventVerb.CREATE) {
			// set organizer
			entity.setOrganizer(actor);
			
			// save meeting in project
			Project project = entity.getProject().get();
			project.addMeeting(entity);
			projectDAO.save(project);
		}
		
	
		if (entity.isScheduled()) this.saveCalendarEvent(entity);
		if (oldEntity!=null && oldEntity.isScheduled() && !entity.isScheduled()) this.removeCalendarEvent(oldEntity);
		
		if (oldEntity!=null && oldEntity.hasPoll())this.removePollEndTask(oldEntity);
		if (entity.hasPoll()) this.spoonPollEndTask(entity);
		
		if (verb == MeetingActivityEventVerb.CREATE) {
				
			DiffNode diffs = ObjectDifferBuilder.buildDefault().compare(entity, oldEntity);
			if (diffs.hasChanges()) {
				Map<String,EntityDiff> entityDiffs = new LinkedHashMap<String,EntityDiff>();
				diffs.visit(new EntityDiffVisitor(entity, oldEntity,entityDiffs));
				activtyEvent.setDiffs(entityDiffs);
			}

		}
		
		dao.save(entity);
		activtyEvent.setMeeting(entity);
		activtyEvent.setActor(actor);
		activityEventService.registerActivityEvent(activtyEvent);
	}
	
	public void remove(String key){		
		this.remove(key,null);
	}
	
	public void remove(String key, User actor){
		Meeting entity = dao.get(key);
		activityEventService.registerActivityEvent(new MeetingActivityEvent(entity,actor, MeetingActivityEventVerb.DELETE));
		dao.remove(key);
	}
	
	public void pollVote(Meeting meeting, MeetingPollVote vote) {
		
		int voteIndex = -1;
		
		for (int i=0; i<meeting.getPoll().getVotes().size(); i++) {
			MeetingPollVote curVote = meeting.getPoll().getVotes().get(i);
			if (curVote.getUser().equivalent(vote.getUser()) && curVote.getProposedDate().equals(vote.getProposedDate()))
				voteIndex = i;
		}
		
		if (voteIndex>=0) meeting.getPoll().getVotes().remove(voteIndex);
			
		meeting.getPoll().getVotes().add(vote);
		
		MeetingActivityEvent activtyEvent = new MeetingActivityEvent();
		activtyEvent.setMeeting(meeting);
		activtyEvent.setActor(vote.getUser());
		activtyEvent.setVerb(MeetingActivityEventVerb.VOTE);
		activityEventService.registerActivityEvent(activtyEvent);
		
		dao.save(meeting);
	}
	
	public void pollEnd(Meeting meeting) {
		MeetingPollProposedDate preferredDate = meeting.getPoll().getPreferredDate();
		if (preferredDate != null) {
			meeting.setStart(preferredDate.getStart());
			meeting.setEnd(preferredDate.getEnd());
			meeting.getPoll().setEndDate(new Date());
			this.saveCalendarEvent(meeting);
			dao.save(meeting);
			
			MeetingActivityEvent activtyEvent = new MeetingActivityEvent();
			activtyEvent.setMeeting(meeting);
			activtyEvent.setActor(meeting.getOrganizer());
			activtyEvent.setVerb(MeetingActivityEventVerb.SCHEDULE);
			activityEventService.registerActivityEvent(activtyEvent);
			
			// send notifications
			try {
				this.sendMeetingScheduledNotification(meeting, meeting.getOrganizer().get());
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (MessagingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		} else {
			// TODO: notify the organizer, no votes!
		}
	}
	
	private MeetingActivityEventVerb getSaveActivityVerb(Meeting entity, User actor) {
		if (entity.getId() == null)
			return MeetingActivityEventVerb.CREATE;
		else {
			
			Meeting oldEntity = dao.get(entity.getKey());
			
			if (!oldEntity.isScheduled() && entity.isScheduled())
				return MeetingActivityEventVerb.SCHEDULE;
			else if (!entity.isScheduled() && entity.hasPoll() && !oldEntity.hasPoll())
				return MeetingActivityEventVerb.POLL;
			else
				return MeetingActivityEventVerb.EDIT;
		}
	}
	
	private void saveCalendarEvent(Meeting meeting) {
		try {
			
			Calendar calendarService = GoogleServices.getCalendarServiceDomainWide(meeting.getOrganizer().get());
			
			String calendarId = meeting.getOrganizer().get().getEmail();
			
			Event event;
			if (meeting.getCalendarEventId() == null) {
				event = new Event();
				
				EventAttendee teamAttendee = new EventAttendee().setEmail(meeting.getProject().get().getGroupEmail());
				event.setAttendees(Arrays.asList(teamAttendee));
				
			} else {
				event = calendarService.events().get(calendarId, meeting.getCalendarEventId()).execute();
			}
			
			event.setSummary("Meeting: "+meeting.getTitle());
			event.setDescription(meeting.getDescription());
			
			event.setStart(new EventDateTime().setDateTime(new DateTime(meeting.getStart())));
			event.setEnd(new EventDateTime().setDateTime(new DateTime(new Date(meeting.getEnd().getTime()))));
			
			if (meeting.getCalendarEventId() == null) {
				event = calendarService.events().insert(calendarId, event).execute();
				meeting.setCalendarEventId(event.getId());
			} else {
				event = calendarService.events().update(calendarId, event.getId(), event).execute();
			}
			
			meeting.setHangoutLink(event.getHangoutLink());
			
		} catch (GeneralSecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void removeCalendarEvent(Meeting meeting) {
		try {
			
			Calendar calendarService = GoogleServices.getCalendarServiceDomainWide(meeting.getOrganizer().get());
			
			String calendarId = meeting.getOrganizer().get().getEmail();
			
			if (meeting.getCalendarEventId() != null)
				calendarService.events().delete(calendarId, meeting.getCalendarEventId()).execute();
			
			meeting.setCalendarEventId(null);
			
			
		} catch (GeneralSecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void spoonPollEndTask(Meeting meeting) {
		
		String taskName = "pollEnd_"+meeting.getKey();
		
		this.removePollEndTask(meeting);
		
		if (meeting.getPoll().getEndDate() != null && !meeting.getPoll().isEnded()) {
					
			Queue queue = QueueFactory.getDefaultQueue();
		    TaskOptions task = TaskOptions.Builder
		    	.withUrl(API.getBaseUrlWithoutHostAndSchema()+"/gae/task/pollEnd")
		    	.etaMillis(meeting.getPoll().getEndDate().getTime())
		    	.param("meeting", meeting.getKey())
		    	.method(Method.POST)
		    	.taskName(taskName)
		    ; 
		    
	        queue.add(task);
        
		}
	}
	
	private void removePollEndTask(Meeting meeting) {
		String taskName = "pollEnd_"+meeting.getKey();
		
		Queue queue = QueueFactory.getDefaultQueue();
		queue.deleteTask(taskName);
	}
	
	private void sendMeetingScheduledNotification(Meeting meeting, User actor) throws UnsupportedEncodingException, MessagingException {
		
		String actionUrl = AppHelper.getBaseUrl()+"/#/project/"+meeting.getProject().get().getKey()+"/meeting/"+meeting.getKey();
		
		String subject = meeting.getProject().get().getName()+": meeting "+meeting.getTitle();
		
		Map<String, Object> data = new LinkedHashMap<String, Object>();
		data.put("header",subject);
		data.put("body",actor.getFirstName()+" scheduled a meeting");
		data.put("actorPhoto", actor.getIconUrl());
		data.put("actionLabel","Open Meeting");
		data.put("actionUrl",actionUrl);
		
		String mailHtml = TemplatingService.getInstance().compile(data, "base.html.vm");
		String mailPlaintext = TemplatingService.getInstance().compile(data, "base.txt.vm");
		
		NotificationService.getInstance().sendMessage(subject, mailPlaintext, mailHtml, meeting.getProject().get(), actor);
		
	}
	
	private void sendMeetingPollNotification(Meeting meeting, User actor) throws UnsupportedEncodingException, MessagingException {
		
		String actionUrl = AppHelper.getBaseUrl()+"/#/project/"+meeting.getProject().get().getKey()+"/meetings";
		
		String subject = meeting.getProject().get().getName()+": meeting "+meeting.getTitle();
		
		Map<String, Object> data = new LinkedHashMap<String, Object>();
		data.put("header",subject);
		data.put("body",actor.getFirstName()+" opened a poll. Please vote for your preferred dates.");
		data.put("actorPhoto", actor.getIconUrl());
		data.put("actionLabel","Vote dates");
		data.put("actionUrl",actionUrl);
		
		String mailHtml = TemplatingService.getInstance().compile(data, "base.html.vm");
		String mailPlaintext = TemplatingService.getInstance().compile(data, "base.txt.vm");
		
		NotificationService.getInstance().sendMessage(subject, mailPlaintext, mailHtml, meeting.getProject().get(), actor);
		
	}

}
