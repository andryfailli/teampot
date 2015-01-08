package com.google.teampot.service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.mail.MessagingException;

import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.teampot.GoogleServices;
import com.google.teampot.dao.ProjectDAO;
import com.google.teampot.dao.TaskDAO;
import com.google.teampot.diff.visitor.EntityDiffVisitor;
import com.google.teampot.model.EntityDiff;
import com.google.teampot.model.Project;
import com.google.teampot.model.Task;
import com.google.teampot.model.TaskActivityEvent;
import com.google.teampot.model.TaskActivityEventVerb;
import com.google.teampot.model.User;
import com.google.teampot.util.AppHelper;
import com.google.teampot.util.GoogleCalendarHelper;
import com.googlecode.objectify.Ref;

import de.danielbechler.diff.ObjectDifferBuilder;
import de.danielbechler.diff.node.DiffNode;

public class TaskService{

	private static TaskService instance;
	
	private TaskDAO dao;
	private ProjectDAO projectDAO;
	private static ActivityEventService activityEventService = ActivityEventService.getInstance();
	
	private TaskService() {
		this.dao = new TaskDAO();
		this.projectDAO = new ProjectDAO();
	}
	
	public static TaskService getInstance() {
		if (instance == null) instance = new TaskService();
		return instance;
	}
	
	public List<Task> list(String projectId) {
		return dao.list(projectId);
	}
	
	public List<Task> list() {
		return dao.list();
	}
	
	public List<Task> listForUser(User assignee) {
		return dao.listForUser(assignee);
	}
	
	public List<Task> listToDoForUser(User assignee) {
		return dao.listToDoForUser(assignee);
	}
	
	public Task get(String key){
		return dao.get(key);
	}
	
	public void save(Task entity){
		this.save(entity,null);
	}
	
	public void save(Task entity, User actor){
		TaskActivityEvent activtyEvent = new TaskActivityEvent();
				
		TaskActivityEventVerb verb = this.getSaveActivityVerb(entity,actor);
		activtyEvent.setVerb(verb);
		
		Task oldEntity = null;
		if (verb != TaskActivityEventVerb.CREATE) {
			oldEntity = dao.get(entity.getKey());
			
			// persist DueDateCalendarEventId
			entity.setDueDateCalendarEventId(oldEntity.getDueDateCalendarEventId());
		}
		
		// first save
		dao.save(entity);
		
		// send notifications
		try {
			switch (verb) {
			case CREATE:
				if (entity.isAssigned()) this.sendTaskAssignNotification(entity,actor);
				break;
			case COMPLETE:
			case EDIT:
				if (entity.isAssigned() && !entity.getAssignee().equals(Ref.create(actor))) this.sendTaskUpdatedNotification(entity,actor);
				break;
			case ASSIGN:
				if ((!oldEntity.isAssigned() && entity.isAssigned()) || ( oldEntity.isAssigned() && entity.isAssigned() && !oldEntity.getAssignee().equals(entity.getAssignee()))) this.sendTaskAssignNotification(entity,actor);
				break;
			case UNASSIGN:
				this.sendTaskUnassignNotification(oldEntity,actor);
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
		
		
		if (verb == TaskActivityEventVerb.CREATE) {
			Project project = entity.getProject().get();
			project.addTask(entity);
			projectDAO.save(project);
		}
		
		
		if (verb != TaskActivityEventVerb.CREATE)  {
			DiffNode diffs = ObjectDifferBuilder.buildDefault().compare(entity, oldEntity);
			if (diffs.hasChanges()) {
				Map<String,EntityDiff> entityDiffs = new LinkedHashMap<String,EntityDiff>();
				diffs.visit(new EntityDiffVisitor(entity, oldEntity,entityDiffs));
				activtyEvent.setDiffs(entityDiffs);
			}
		}
		
		if (verb == TaskActivityEventVerb.ASSIGN || verb == TaskActivityEventVerb.UNASSIGN)  {
			this.removeCalendarEvent(oldEntity);
		
		}
		this.saveCalendarEvent(entity);
		
		dao.save(entity);
		activtyEvent.setTask(entity);
		activtyEvent.setActor(actor);
		activityEventService.registerActivityEvent(activtyEvent);
	}

	public void remove(String key){		
		this.remove(key,null);
	}
	
	public void remove(String key, User actor){
		Task entity = dao.get(key);
		activityEventService.registerActivityEvent(new TaskActivityEvent(entity,actor, TaskActivityEventVerb.DELETE));
		dao.remove(key);
	}
	
	private TaskActivityEventVerb getSaveActivityVerb(Task entity, User actor) {
		if (entity.getId() == null)
			return TaskActivityEventVerb.CREATE;
		else {
			
			Task oldEntity = dao.get(entity.getKey());
			
			if (entity.isCompleted() && !oldEntity.isCompleted())
				return TaskActivityEventVerb.COMPLETE;
			else if ((oldEntity.getAssignee() == null && entity.getAssignee() != null) || (oldEntity.getAssignee() != null && !oldEntity.getAssignee().equals(entity.getAssignee())))
				return TaskActivityEventVerb.ASSIGN;
			else if (oldEntity.getAssignee() != null && entity.getAssignee() == null)
				return TaskActivityEventVerb.UNASSIGN;
			else
				return TaskActivityEventVerb.EDIT;
		}
	}
	
	private void saveCalendarEvent(Task task) {
		if (task.getAssignee() != null && task.getDueDate() != null && task.getAssignee().get().hasLoggedIn()) {
			try {
				Calendar calendarService = GoogleServices.getCalendarService(task.getAssignee().get());
				
				String calendarId = task.getAssignee().get().getEmail();
				
				Event event;				
				if (task.getDueDateCalendarEventId() == null) {
					event = new Event();					
				} else {
					event = calendarService.events().get(calendarId, task.getDueDateCalendarEventId()).execute();
				}
				
				event.setSummary((task.isCompleted() ? "✔" : "▢") + " Task: "+task.getTitle());
				event.setDescription(task.getDescription());
				GoogleCalendarHelper.setAllDayEvent(task.getDueDate(), event);
				
				
				if (task.getDueDateCalendarEventId() == null) {
					event = calendarService.events().insert(calendarId, event).execute();
					task.setDueDateCalendarEventId(event.getId());
				} else {
					event = calendarService.events().update(calendarId, event.getId(), event).execute();
				}
				
			} catch (GeneralSecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private void removeCalendarEvent(Task task) {
		if (task.getAssignee() != null && task.getDueDateCalendarEventId() != null  && task.getAssignee().get().hasLoggedIn()) {
			try {
				Calendar calendarService = GoogleServices.getCalendarService(task.getAssignee().get());
				calendarService.events().delete(task.getAssignee().get().getEmail(), task.getDueDateCalendarEventId()).execute();
				
				task.setDueDateCalendarEventId(null);
				
			} catch (GeneralSecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void createCalendarEventsForUser(User user) {
		for (Task task : this.listForUser(user)) {
			this.saveCalendarEvent(task);
		}
	}
	
	private void sendTaskUnassignNotification(Task oldEntity, User actor) throws UnsupportedEncodingException, MessagingException {
		String actionUrl = AppHelper.getBaseUrl()+"/#/project/"+oldEntity.getProject().get().getKey()+"/task/"+oldEntity.getKey();
		
		String subject = oldEntity.getProject().get().getName()+": task "+oldEntity.getTitle();
		
		Map<String, Object> data = new LinkedHashMap<String, Object>();
		data.put("header",subject);
		data.put("body",actor.getFirstName()+" has removed the task assignment to you.");
		data.put("actorPhoto", actor.getIconUrl());
		data.put("actionLabel","View Task");
		data.put("actionUrl",actionUrl);
		
		String mailHtml = TemplatingService.getInstance().compile(data, "base.html.vm");
		String mailPlaintext = TemplatingService.getInstance().compile(data, "base.txt.vm");
		
		NotificationService.getInstance().sendMessage(subject, mailPlaintext, mailHtml, oldEntity.getAssignee().get(), actor);
		
	}

	private void sendTaskAssignNotification(Task entity, User actor) throws UnsupportedEncodingException, MessagingException {
		String actionUrl = AppHelper.getBaseUrl()+"/#/project/"+entity.getProject().get().getKey()+"/task/"+entity.getKey();
		
		String subject = entity.getProject().get().getName()+": task "+entity.getTitle();
		
		Map<String, Object> data = new LinkedHashMap<String, Object>();
		data.put("header",subject);
		data.put("body",actor.getFirstName()+" has assigned a task to you.");
		data.put("actorPhoto", actor.getIconUrl());
		data.put("actionLabel","View Task");
		data.put("actionUrl",actionUrl);
		
		String mailHtml = TemplatingService.getInstance().compile(data, "base.html.vm");
		String mailPlaintext = TemplatingService.getInstance().compile(data, "base.txt.vm");
		
		NotificationService.getInstance().sendMessage(subject, mailPlaintext, mailHtml, entity.getAssignee().get(), actor);
	}
	
	private void sendTaskUpdatedNotification(Task entity, User actor) throws UnsupportedEncodingException, MessagingException {
		String actionUrl = AppHelper.getBaseUrl()+"/#/project/"+entity.getProject().get().getKey()+"/task/"+entity.getKey();
		
		String subject = entity.getProject().get().getName()+": task "+entity.getTitle();
		
		Map<String, Object> data = new LinkedHashMap<String, Object>();
		data.put("header",subject);
		data.put("body",actor.getFirstName()+" has updated a task assigned to you.");
		data.put("actorPhoto", actor.getIconUrl());
		data.put("actionLabel","View Task");
		data.put("actionUrl",actionUrl);
		
		String mailHtml = TemplatingService.getInstance().compile(data, "base.html.vm");
		String mailPlaintext = TemplatingService.getInstance().compile(data, "base.txt.vm");
		
		NotificationService.getInstance().sendMessage(subject, mailPlaintext, mailHtml, entity.getAssignee().get(), actor);
	}

}
