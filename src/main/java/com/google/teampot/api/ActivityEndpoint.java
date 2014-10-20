package com.google.teampot.api;

import java.util.List;

import com.google.teampot.api.exception.EntityNotFoundException;
import com.google.teampot.dao.ActivityEventDAO;
import com.google.teampot.dao.TaskDAO;
import com.google.teampot.model.ActivityEvent;
import com.google.teampot.model.Task;
import com.google.teampot.model.TaskActivityEventVerb;
import com.google.teampot.service.ActivityEventService;
import com.google.teampot.service.UserService;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiMethod.HttpMethod;
import com.google.api.server.spi.config.Named;
import com.google.appengine.api.oauth.OAuthRequestException;

public class ActivityEndpoint extends BaseEndpoint {

	private static ActivityEventDAO dao = new ActivityEventDAO();
	
	@ApiMethod(
		name = "activity.list",
		path = "activity",
		httpMethod = HttpMethod.GET
	)
	public List<ActivityEvent> list(com.google.appengine.api.users.User gUser, @Named("project") String projectId) throws OAuthRequestException {
		UserService.getInstance().ensureProvisioning(gUser);
		
		return dao.list(projectId);
	}
	
	@ApiMethod(
		name = "activity.get", 
		path = "activity/{id}",
		httpMethod = HttpMethod.GET
	)
	public ActivityEvent get(@Named("id") String id, com.google.appengine.api.users.User gUser) throws OAuthRequestException,EntityNotFoundException {
		UserService.getInstance().ensureProvisioning(gUser);
		
		ActivityEvent entity = dao.get(id);
		if (entity != null)
			return entity;
		else
			throw new EntityNotFoundException(id);
	}
	
	@ApiMethod(
		name = "activity.save",
		path = "activity",
		httpMethod = HttpMethod.POST
	)
	public ActivityEvent save(ActivityEvent entity, com.google.appengine.api.users.User gUser) throws OAuthRequestException {
		UserService.getInstance().ensureProvisioning(gUser);
		
		dao.save(entity);
		return entity;
	}
	
	@ApiMethod(
		name = "activity.remove",
		path = "activity/{id}",
		httpMethod = HttpMethod.DELETE
	)
	public void remove(@Named("id") String id, com.google.appengine.api.users.User gUser) throws OAuthRequestException {
		UserService.getInstance().ensureProvisioning(gUser);

		dao.remove(id);
	}
	
}