package com.google.teampot.service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.teampot.dao.TaskDAO;
import com.google.teampot.diff.visitor.EntityDiffVisitor;
import com.google.teampot.model.EntityDiff;
import com.google.teampot.model.Task;
import com.google.teampot.model.TaskActivityEvent;
import com.google.teampot.model.TaskActivityEventVerb;
import com.google.teampot.model.User;

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

}
