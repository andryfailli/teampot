package com.google.teampot.service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.teampot.GoogleServices;
import com.google.teampot.dao.TaskDAO;
import com.google.teampot.diff.visitor.EntityDiffVisitor;
import com.google.teampot.model.EntityDiff;
import com.google.teampot.model.Task;
import com.google.teampot.model.TaskActivityEvent;
import com.google.teampot.model.TaskActivityEventVerb;
import com.google.teampot.model.User;
import com.google.teampot.util.GoogleCalendarHelper;

import de.danielbechler.diff.ObjectDifferBuilder;
import de.danielbechler.diff.node.DiffNode;

public class TaskService{

	private static TaskService instance;
	
	private TaskDAO dao; 
	private static ActivityEventService activityEventService = ActivityEventService.getInstance();
	
	private TaskService() {
		this.dao = new TaskDAO();
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
	
	public Task get(String key){
		return dao.get(key);
	}
	
	public void save(Task entity){
		this.save(entity,null);
	}
	
	public void save(Task entity, User actor){
		TaskActivityEvent activtyEvent = new TaskActivityEvent();
		if (entity.getId() == null) {
			activtyEvent.setVerb(TaskActivityEventVerb.CREATE);
		} else {
			
			Task oldEntity = dao.get(entity.getKey());
			
			if (entity.isCompleted() && !oldEntity.isCompleted())
				activtyEvent.setVerb(TaskActivityEventVerb.COMPLETE);
			else
				activtyEvent.setVerb(TaskActivityEventVerb.EDIT);
			
			// TODO: recreate event only if necessary
			this.removeCalendarEvent(oldEntity);
			this.addCalendarEvent(entity);
			
			DiffNode diffs = ObjectDifferBuilder.buildDefault().compare(entity, oldEntity);
			if (diffs.hasChanges()) {
				Map<String,EntityDiff> entityDiffs = new LinkedHashMap<String,EntityDiff>();
				diffs.visit(new EntityDiffVisitor(entity, oldEntity,entityDiffs));
				activtyEvent.setDiffs(entityDiffs);
			}

		}
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
	
	private void addCalendarEvent(Task task) {
		if (task.getAssignee() != null && task.getDueDate() != null) {
			try {
				Calendar calendarService = GoogleServices.getCalendarServiceDomainWide(task.getAssignee().get());
				
				Event event = new Event();
				event.setSummary((task.isCompleted() ? "✔" : "▢") + " Task: "+task.getTitle());
				event.setDescription(task.getDescription());
				
				GoogleCalendarHelper.setAllDayEvent(task.getDueDate(), event);
				
				event = calendarService.events().insert(task.getAssignee().get().getEmail(), event).execute();
				task.setDueDateCalendarEventId(event.getId());
				
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
		if (task.getAssignee() != null && task.getDueDateCalendarEventId() != null) {
			try {
				Calendar calendarService = GoogleServices.getCalendarServiceDomainWide(task.getAssignee().get());
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

}
