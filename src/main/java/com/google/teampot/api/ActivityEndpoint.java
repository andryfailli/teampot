package com.google.teampot.api;

import java.util.List;

import com.google.teampot.api.exception.EntityNotFoundException;
import com.google.teampot.dao.ActivityEventDAO;
import com.google.teampot.model.ActivityEvent;
import com.google.teampot.model.MetricsSnapshot;
import com.google.teampot.model.Project;
import com.google.teampot.service.AnalyticsService;
import com.google.teampot.service.UserService;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiMethod.HttpMethod;
import com.google.api.server.spi.config.Named;
import com.google.api.server.spi.response.UnauthorizedException;
import com.google.appengine.api.oauth.OAuthRequestException;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Ref;

public class ActivityEndpoint extends BaseEndpoint {

	private static ActivityEventDAO dao = new ActivityEventDAO();
	
	@ApiMethod(
		name = "activity.list",
		path = "activity",
		httpMethod = HttpMethod.GET
	)
	public List<ActivityEvent> list(com.google.appengine.api.users.User gUser, @Named("project") String projectId, @Named("page") Integer page) throws OAuthRequestException, UnauthorizedException {
		UserService.getInstance().ensureEnabled(gUser);
		
		if (page != null)
			return dao.list(projectId,page);
		else
			return dao.list(projectId);
	}
	
	@ApiMethod(
		name = "activity.get", 
		path = "activity/{id}",
		httpMethod = HttpMethod.GET
	)
	public ActivityEvent get(@Named("id") String id, com.google.appengine.api.users.User gUser) throws OAuthRequestException,EntityNotFoundException, UnauthorizedException {
		UserService.getInstance().ensureEnabled(gUser);
		
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
	public ActivityEvent save(ActivityEvent entity, com.google.appengine.api.users.User gUser) throws OAuthRequestException, UnauthorizedException {
		UserService.getInstance().ensureEnabled(gUser);
		
		dao.save(entity);
		return entity;
	}
	
	@ApiMethod(
		name = "activity.remove",
		path = "activity/{id}",
		httpMethod = HttpMethod.DELETE
	)
	public void remove(@Named("id") String id, com.google.appengine.api.users.User gUser) throws OAuthRequestException, UnauthorizedException {
		UserService.getInstance().ensureEnabled(gUser);

		dao.remove(id);
	}
	
	@ApiMethod(
		name = "activity.analytics", 
		path = "activity/analytics",
		httpMethod = HttpMethod.GET
	)
	public MetricsSnapshot analytics(@Named("project") String projectId, com.google.appengine.api.users.User gUser) throws OAuthRequestException,EntityNotFoundException, UnauthorizedException {
		UserService.getInstance().ensureEnabled(gUser);
		
		Key<Project> projectKey = Key.create(projectId);
		Ref<Project> project = Ref.create(projectKey);
		return AnalyticsService.getInstance().getMetrics(project);
	}
	
}