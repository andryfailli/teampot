package com.google.teampot.api;

import java.util.List;

import com.google.teampot.api.exception.EntityNotFoundException;
import com.google.teampot.dao.TaskDAO;
import com.google.teampot.model.Task;
import com.google.teampot.model.TaskActivityEventVerb;
import com.google.teampot.service.ActivityEventService;
import com.google.teampot.service.UserService;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiMethod.HttpMethod;
import com.google.api.server.spi.config.Named;
import com.google.appengine.api.oauth.OAuthRequestException;

public class TaskEndpoint extends BaseEndpoint {

	private static TaskDAO dao = new TaskDAO();
	
	@ApiMethod(
		name = "task.list",
		path = "task",
		httpMethod = HttpMethod.GET
	)
	public List<Task> list(com.google.appengine.api.users.User gUser, @Named("project") String projectId) throws OAuthRequestException {
		UserService.getInstance().ensureProvisioning(gUser);
		
		return dao.list(projectId);
	}
	
	@ApiMethod(
		name = "task.get", 
		path = "task/{id}",
		httpMethod = HttpMethod.GET
	)
	public Task get(@Named("id") String id, com.google.appengine.api.users.User gUser) throws OAuthRequestException,EntityNotFoundException {
		UserService.getInstance().ensureProvisioning(gUser);
		
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
	public Task save(Task entity, com.google.appengine.api.users.User gUser) throws OAuthRequestException {
		UserService.getInstance().ensureProvisioning(gUser);
		
		TaskActivityEventVerb taskActivityEventVerb;
		if (entity.getId() == null) {
			taskActivityEventVerb = TaskActivityEventVerb.CREATE;
		} else {
			taskActivityEventVerb = TaskActivityEventVerb.EDIT;
		}
		dao.save(entity);
		ActivityEventService.getInstance().registerTaskActivityEvent(entity, gUser, taskActivityEventVerb);
		return entity;
	}
	
	@ApiMethod(
		name = "task.remove",
		path = "task/{id}",
		httpMethod = HttpMethod.DELETE
	)
	public void remove(@Named("id") String id, com.google.appengine.api.users.User gUser) throws OAuthRequestException {
		UserService.getInstance().ensureProvisioning(gUser);
		
		Task entity = dao.get(id);
		ActivityEventService.getInstance().registerTaskActivityEvent(entity, gUser, TaskActivityEventVerb.DELETE);
		dao.remove(id);
	}
	
}