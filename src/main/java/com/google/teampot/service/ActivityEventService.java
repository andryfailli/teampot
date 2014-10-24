package com.google.teampot.service;

import java.util.Date;

import com.google.teampot.dao.ActivityEventDAO;
import com.google.teampot.model.ActivityEvent;

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
	
	public ActivityEvent registerActivityEvent(ActivityEvent activtyEvent) {
		this.saveActivityEvent(activtyEvent);
		return activtyEvent;
	}
}
