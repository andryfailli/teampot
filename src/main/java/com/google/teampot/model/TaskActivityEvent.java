package com.google.teampot.model;

import com.google.api.server.spi.config.AnnotationBoolean;
import com.google.api.server.spi.config.ApiResourceProperty;
import com.google.teampot.transformer.Ref2EntityTransformer;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Load;
import com.googlecode.objectify.annotation.Subclass;

@Subclass(index=true)
public class TaskActivityEvent extends ActivityEvent {

	@Load
	private Ref<Task> task;

	public TaskActivityEvent() {
		// TODO Auto-generated constructor stub
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

}
