package com.google.teampot.api;

import java.util.List;

import com.google.teampot.api.exception.EntityNotFoundException;
import com.google.teampot.model.Task;
import com.google.teampot.model.User;
import com.google.teampot.service.TaskService;
import com.google.teampot.service.UserService;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiMethod.HttpMethod;
import com.google.api.server.spi.config.Named;
import com.google.api.server.spi.config.Nullable;
import com.google.api.server.spi.response.UnauthorizedException;
import com.google.appengine.api.oauth.OAuthRequestException;

public class TaskEndpoint extends BaseEndpoint {

	private static TaskService taskService = TaskService.getInstance();
	private static UserService userService = UserService.getInstance();
	
	@ApiMethod(
		name = "task.list",
		path = "task",
		httpMethod = HttpMethod.GET
	)
	public List<Task> list(com.google.appengine.api.users.User gUser, @Nullable @Named("project") String projectId, @Nullable @Named("user") String userId) throws OAuthRequestException, UnauthorizedException, EntityNotFoundException {
		userService.ensureEnabled(gUser);
		
		if (projectId != null && userId == null)
			return taskService.list(projectId);
		else if (projectId == null && userId != null) {
			User assignee = userService.get(userId);
			if (assignee == null) throw new EntityNotFoundException("User "+userId+" not found");
			return taskService.listToDoForUser(assignee);
		} else if (projectId == null && userId == null) {
			return taskService.list();
		} else {
			throw new IllegalArgumentException("Please, specify only one of project or user, not both");
		}
			
	}
	
	@ApiMethod(
		name = "task.get", 
		path = "task/{id}",
		httpMethod = HttpMethod.GET
	)
	public Task get(@Named("id") String id, com.google.appengine.api.users.User gUser) throws OAuthRequestException,EntityNotFoundException, UnauthorizedException {
		userService.ensureEnabled(gUser);
		
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
	public Task save(Task entity, com.google.appengine.api.users.User gUser) throws OAuthRequestException, UnauthorizedException {
		userService.ensureEnabled(gUser);
		
		taskService.save(entity, userService.getUser(gUser));
		return entity;
	}
	
	@ApiMethod(
		name = "task.remove",
		path = "task/{id}",
		httpMethod = HttpMethod.DELETE
	)
	public void remove(@Named("id") String id, com.google.appengine.api.users.User gUser) throws OAuthRequestException, UnauthorizedException {
		userService.ensureEnabled(gUser);
		
		taskService.remove(id, userService.getUser(gUser));
	}
	
}