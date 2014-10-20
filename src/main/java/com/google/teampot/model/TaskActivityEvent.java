package com.google.teampot.model;

import com.google.api.server.spi.config.AnnotationBoolean;
import com.google.api.server.spi.config.ApiResourceProperty;
import com.google.teampot.transformer.Enum2StringTransformer;
import com.google.teampot.transformer.Ref2EntityTransformer;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Load;
import com.googlecode.objectify.annotation.Subclass;

@Subclass(index=true)
public class TaskActivityEvent extends ActivityEvent {

	@Load
	private Ref<Task> task;
	
	private TaskActivityEventVerb verb;

	public TaskActivityEvent() {
		// TODO Auto-generated constructor stub
	}
	
	public TaskActivityEvent(Task task, User actor, TaskActivityEventVerb verb) {
		super(task.getProject());
		this.setTask(task);
		this.setActor(actor);
		this.setVerb(verb);
	}

	@ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
	public Ref<Task> getTask() {
		return task;
	}

	@ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
	public void setTask(Ref<Task> task) {
		this.task = task;
	}
	
	@ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
	public void setTask(Task task) {
		this.task = Ref.create(task);
	}
	
	@ApiResourceProperty(name = "task")
	public Task getTaskEntity() {
		Ref2EntityTransformer<Task> t = new Ref2EntityTransformer<Task>();
		return t.transformTo(this.task);
	}	

	@ApiResourceProperty(name = "task")
	public void setTaskEntity(Task task) {
		Ref2EntityTransformer<Task> t = new Ref2EntityTransformer<Task>();
		this.task = t.transformFrom(task);
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
	public String getVerbString() {
		Enum2StringTransformer<TaskActivityEventVerb> t = new Enum2StringTransformer<TaskActivityEventVerb>(TaskActivityEventVerb.class);
		return t.transformTo(this.verb);
	}	

	@ApiResourceProperty(name = "verb")
	public void setVerbString(String verb) {
		Enum2StringTransformer<TaskActivityEventVerb> t = new Enum2StringTransformer<TaskActivityEventVerb>(TaskActivityEventVerb.class);
		this.verb = t.transformFrom(verb);
	}

}
