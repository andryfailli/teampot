package com.google.teampot.service;

import java.util.Date;

import com.google.teampot.dao.ActivityEventDAO;
import com.google.teampot.model.ActivityEvent;
import com.google.teampot.model.Task;
import com.google.teampot.model.TaskActivityEvent;
import com.google.teampot.model.TaskActivityEventVerb;
import com.google.teampot.model.User;

public class ActivityEventService {

	private static ActivityEventService instance;
	
	private ActivityEventDAO dao; 
	
	private ActivityEventService() {
		this.dao = new ActivityEventDAO();
	}
	
	public static ActivityEventService getInstance() {
		if (instance == null) instance = new ActivityEventService();
		return instance;
	}
	
	private void saveActivityEvent(ActivityEvent activtyEvent) {
		activtyEvent.setTimestamp(new Date());
		dao.save(activtyEvent);
	}
	
	public TaskActivityEvent registerTaskActivityEvent(TaskActivityEvent activtyEvent) {
		this.saveActivityEvent(activtyEvent);
		return activtyEvent;
	}
	public TaskActivityEvent registerTaskActivityEvent(Task task, User actor, TaskActivityEventVerb verb) {
		TaskActivityEvent activtyEvent = new TaskActivityEvent(task, actor, verb);
		this.registerTaskActivityEvent(activtyEvent);
		return activtyEvent;
	}
	public TaskActivityEvent registerTaskActivityEvent(Task task, com.google.appengine.api.users.User gUser, TaskActivityEventVerb verb) {
		return this.registerTaskActivityEvent(task, UserService.getInstance().getUser(gUser),verb);
	}

}
