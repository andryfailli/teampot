package com.google.teampot.dao;

import static com.google.teampot.OfyService.ofy;

import java.util.List;

import com.google.teampot.model.Task;
import com.google.teampot.model.User;

public class TaskDAO extends BaseEntityDAO<Task>{
	
	public TaskDAO() {
		super(Task.class);
	}
	
	public List<Task> listForUser(User assignee) {
		return ofy().load().type(Task.class).filter("assignee",assignee).list();
	}
	
	public List<Task> listToDoForUser(User assignee) {
		return ofy().load().type(Task.class).filter("assignee",assignee).filter("completed",false).list();
	}

}
