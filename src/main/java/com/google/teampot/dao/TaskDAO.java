package com.google.teampot.dao;

import com.google.teampot.model.Task;

public class TaskDAO extends BaseEntityDAO<Task>{
	
	public TaskDAO() {
		super(Task.class);
	}

}
