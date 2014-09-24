package com.google.teampot.model;

import com.google.api.server.spi.config.AnnotationBoolean;
import com.google.api.server.spi.config.ApiResourceProperty;
import com.google.teampot.transformer.Ref2StringTransformer;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Subclass;

@Subclass(index=true)
public class TaskComment extends Comment {

	private Ref<Task> task;
	
	public TaskComment() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public String getKey() {
		return Key.create(this.getTask().getKey(),this.getClass(), this.getId()).getString();
	}

	@ApiResourceProperty(name = "task")
	public String getTaskKey() {
		Ref2StringTransformer<Task> t = new Ref2StringTransformer<Task>();
		return t.transformTo(this.task);
	}	

	@ApiResourceProperty(name = "task")
	public void setTaskKey(String task) {
		Ref2StringTransformer<Task> t = new Ref2StringTransformer<Task>();
		this.task = t.transformFrom(task);
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

}
