package com.google.teampot.api;

import java.util.List;

import com.google.teampot.api.exception.EntityNotFoundException;
import com.google.teampot.model.Task;
import com.google.teampot.service.TaskService;
import com.google.teampot.service.UserService;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiMethod.HttpMethod;
import com.google.api.server.spi.config.Named;
import com.google.appengine.api.oauth.OAuthRequestException;

public class TaskEndpoint extends BaseEndpoint {

	private static TaskService taskService = TaskService.getInstance();
	private static UserService userService = UserService.getInstance();
	
	@ApiMethod(
		name = "task.list",
		path = "task",
		httpMethod = HttpMethod.GET
	)
	public List<Task> list(com.google.appengine.api.users.User gUser, @Named("project") String projectId) throws OAuthRequestException {
		userService.ensureProvisioning(gUser);
		
		return taskService.list(projectId);
	}
	
	@ApiMethod(
		name = "task.get", 
		path = "task/{id}",
		httpMethod = HttpMethod.GET
	)
	public Task get(@Named("id") String id, com.google.appengine.api.users.User gUser) throws OAuthRequestException,EntityNotFoundException {
		userService.ensureProvisioning(gUser);
		
		Task entity = taskService.get(id);
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
	public Task save(Task entity, com.google.appengine.api.users.User gUser) throws OAuthRequestException {
		userService.ensureProvisioning(gUser);
		
		taskService.save(entity, userService.getUser(gUser));
		return entity;
	}
	
	@ApiMethod(
		name = "task.remove",
		path = "task/{id}",
		httpMethod = HttpMethod.DELETE
	)
	public void remove(@Named("id") String id, com.google.appengine.api.users.User gUser) throws OAuthRequestException {
		userService.ensureProvisioning(gUser);
		taskService.remove(id, userService.getUser(gUser));
	}
	
}