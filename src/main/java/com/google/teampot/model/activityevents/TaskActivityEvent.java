package com.google.teampot.model.activityevents;

import com.google.teampot.model.Task;

public class TaskActivityEvent extends ActivityEvent {

	private Task task;

	public TaskActivityEvent() {
		// TODO Auto-generated constructor stub
	}

	public Task getTask() {
		return task;
	}

	public void setTask(Task task) {
		this.task = task;
	}

}
