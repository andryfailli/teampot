package com.google.teampot.model;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Date;

import com.google.api.server.spi.config.AnnotationBoolean;
import com.google.api.server.spi.config.ApiResourceProperty;
import com.google.teampot.transformer.Enum2StringTransformer;
import com.google.teampot.transformer.Ref2EntityTransformer;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Subclass;

@Subclass(index=true)
public class TaskActivityEvent extends ActivityEvent {

	private static final String ADDITIONAL_DATA_DUEDATE_RESPECTED = "task_dueDate_respected";
	
	private TaskActivityEventVerb verb;

	public TaskActivityEvent() {
		// TODO Auto-generated constructor stub
	}
	
	public TaskActivityEvent(Task task, User actor, TaskActivityEventVerb verb) {
		super();
		this.setTask(task);
		this.setActor(actor);
		this.setVerb(verb);
	}
	
	public TaskActivityEvent(Task task, User actor) {
		this(task,actor,null);
	}

	@ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
	public Ref<Task> getTask() {
		return (Ref<Task>) data;
	}

	@ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
	public void setTask(Ref<Task> task) {
		this.data = task;
		this.setProject(task.get().getProject());
	}
	
	@ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
	public void setTask(Task task) {
		this.data = Ref.create(task);
		this.setProject(task.getProject());
	}
	
	@ApiResourceProperty(name = "task")
	public Task getTaskEntity() {
		Ref2EntityTransformer<Task> t = new Ref2EntityTransformer<Task>();
		return t.transformTo((Ref<Task>) this.data);
	}	

	@ApiResourceProperty(name = "task")
	public void setTaskEntity(Task task) {
		Ref2EntityTransformer<Task> t = new Ref2EntityTransformer<Task>();
		this.data = t.transformFrom(task);
	}

	@ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
	public TaskActivityEventVerb getVerb() {
		return verb;
	}

	@ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
	public void setVerb(TaskActivityEventVerb verb) {
		this.verb = verb;
	}
	
	@ApiResourceProperty(name = "verb")
	@Override
	public String getVerbString() {
		Enum2StringTransformer<TaskActivityEventVerb> t = new Enum2StringTransformer<TaskActivityEventVerb>(TaskActivityEventVerb.class);
		return t.transformTo(this.verb);
	}	

	@ApiResourceProperty(name = "verb")
	public void setVerbString(String verb) {
		Enum2StringTransformer<TaskActivityEventVerb> t = new Enum2StringTransformer<TaskActivityEventVerb>(TaskActivityEventVerb.class);
		this.verb = t.transformFrom(verb);
	}
	
	@Override
	public Map<String,String> getAdditionalData() {
		Map<String,String> map = new LinkedHashMap<String, String>();
		
		Task task = this.getTask().get();
		
		// TASK_DUEDATE_RESPECTED
		if (task.getDueDate() != null && task.isCompleted()) {
			Date now = new Date();
			map.put(ADDITIONAL_DATA_DUEDATE_RESPECTED,String.valueOf(task.getDueDate().before(now)).toLowerCase());
		}
		return map;
	}

}
