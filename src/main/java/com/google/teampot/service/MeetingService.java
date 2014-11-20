package com.google.teampot.service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventAttendee;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.appengine.api.taskqueue.TaskOptions.Method;
import com.google.teampot.Config;
import com.google.teampot.GoogleServices;
import com.google.teampot.api.API;
import com.google.teampot.dao.MeetingDAO;
import com.google.teampot.dao.TaskDAO;
import com.google.teampot.diff.visitor.EntityDiffVisitor;
import com.google.teampot.model.EntityDiff;
import com.google.teampot.model.Meeting;
import com.google.teampot.model.MeetingActivityEvent;
import com.google.teampot.model.MeetingActivityEventVerb;
import com.google.teampot.model.MeetingPollVote;
import com.google.teampot.model.Task;
import com.google.teampot.model.TaskActivityEvent;
import com.google.teampot.model.TaskActivityEventVerb;
import com.google.teampot.model.User;

import de.danielbechler.diff.ObjectDifferBuilder;
import de.danielbechler.diff.node.DiffNode;

public class MeetingService{

	private static MeetingService instance;
	
	private MeetingDAO dao; 
	private static ActivityEventService activityEventService = ActivityEventService.getInstance();
	
	private MeetingService() {
		this.dao = new MeetingDAO();
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
		if (entity.getId() == null) {
			activtyEvent.setVerb(MeetingActivityEventVerb.CREATE);
			entity.setOrganizer(actor);
		} else {
			
			Meeting oldEntity = dao.get(entity.getKey());
			
			activtyEvent.setVerb(MeetingActivityEventVerb.EDIT);
			
			
			if (entity.getTimestamp() != null) {
				this.saveCalendarEvent(entity);
			} else if (oldEntity.getTimestamp() != null) {
				this.removeCalendarEvent(oldEntity);
			}
			
			if (entity.getPoll() != null) {
				this.spoonPollEndTask(entity);
			} else if (oldEntity.getPoll() != null) {
				this.removePollEndTask(oldEntity);
			}
			
			
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
		Date preferredDate = meeting.getPoll().getPreferredDate();
		if (preferredDate != null) {
			meeting.setTimestamp(preferredDate);
			meeting.getPoll().setEndDate(new Date());
			this.saveCalendarEvent(meeting);
			dao.save(meeting);
		} else {
			// TODO: notify the organizer, no votes!
		}
	}
	
	private void saveCalendarEvent(Meeting meeting) {
		try {
			
			Calendar calendarService = GoogleServices.getCalendarServiceDomainWide(meeting.getOrganizer().get());
			
			String calendarId = meeting.getOrganizer().get().getEmail();
			
			Event event;
			if (meeting.getCalendarEventId() == null) {
				event = new Event();
				
				EventAttendee teamAttendee = new EventAttendee().setEmail(meeting.getProject().get().getMachineName()+"@"+Config.get(Config.APPS_DOMAIN));
				event.setAttendees(Arrays.asList(teamAttendee));
				
			} else {
				event = calendarService.events().get(calendarId, meeting.getCalendarEventId()).execute();
			}
			
			event.setSummary("Meeting: "+meeting.getTitle());
			event.setDescription(meeting.getDescription());
			
			// TODO: meeting start time & duration?
			event.setStart(new EventDateTime().setDateTime(new DateTime(meeting.getTimestamp())));
			event.setEnd(new EventDateTime().setDateTime(new DateTime(new Date(meeting.getTimestamp().getTime()+3600000))));
			
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
			
			calendarService.events().delete(calendarId, meeting.getCalendarEventId()).execute();
			
			
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

}
