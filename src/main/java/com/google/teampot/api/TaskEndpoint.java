package com.google.teampot.api;

import java.util.List;

import com.google.teampot.api.exception.EntityNotFoundException;
import com.google.teampot.dao.TaskDAO;
import com.google.teampot.model.Task;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiMethod.HttpMethod;
import com.google.api.server.spi.config.Named;

public class TaskEndpoint extends BaseEndpoint {

	private static TaskDAO dao = new TaskDAO();
	
	@ApiMethod(
		name = "task.list",
		path = "task",
		httpMethod = HttpMethod.GET
	)
	public List<Task> list() {
		return dao.list();
	}
	
	@ApiMethod(
		name = "task.get", 
		path = "task/{id}",
		httpMethod = HttpMethod.GET
	)
	public Task get(@Named("id") String id) throws EntityNotFoundException {
		Task entity = dao.get(id);
		if (entity != null)
			return entity;
		else
			throw new EntityNotFoundException(id);
	}
	
	@ApiMethod(
		name = "task.save",
		path = "task",
		httpMethod = HttpMethod.POST
	)
	public Task save(Task entity) {
		dao.save(entity);
		return entity;
	}
	
	@ApiMethod(
		name = "task.remove",
		path = "task/{id}",
		httpMethod = HttpMethod.DELETE
	)
	public void remove(@Named("id") String id) {
		dao.remove(id);
	}
	
}