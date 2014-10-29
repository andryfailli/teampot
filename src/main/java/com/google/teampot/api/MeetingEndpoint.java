package com.google.teampot.api;

import java.util.List;

import com.google.teampot.api.exception.EntityNotFoundException;
import com.google.teampot.model.Meeting;
import com.google.teampot.service.MeetingService;
import com.google.teampot.service.UserService;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiMethod.HttpMethod;
import com.google.api.server.spi.config.Named;
import com.google.appengine.api.oauth.OAuthRequestException;

public class MeetingEndpoint extends BaseEndpoint {

	private static MeetingService meetingService = MeetingService.getInstance();
	private static UserService userService = UserService.getInstance();
	
	@ApiMethod(
		name = "meeting.list",
		path = "meeting",
		httpMethod = HttpMethod.GET
	)
	public List<Meeting> list(com.google.appengine.api.users.User gUser, @Named("project") String projectId) throws OAuthRequestException {
		userService.ensureProvisioning(gUser);
		
		return meetingService.list(projectId);
	}
	
	@ApiMethod(
		name = "meeting.get", 
		path = "meeting/{id}",
		httpMethod = HttpMethod.GET
	)
	public Meeting get(@Named("id") String id, com.google.appengine.api.users.User gUser) throws OAuthRequestException,EntityNotFoundException {
		userService.ensureProvisioning(gUser);
		
		Meeting entity = meetingService.get(id);
		if (entity != null)
			return entity;
		else
			throw new EntityNotFoundException(id);
	}
	
	@ApiMethod(
		name = "meeting.save",
		path = "meeting",
		httpMethod = HttpMethod.POST
	)
	public Meeting save(Meeting entity, com.google.appengine.api.users.User gUser) throws OAuthRequestException {
		userService.ensureProvisioning(gUser);
		
		meetingService.save(entity, userService.getUser(gUser));
		return entity;
	}
	
	@ApiMethod(
		name = "meeting.remove",
		path = "meeting/{id}",
		httpMethod = HttpMethod.DELETE
	)
	public void remove(@Named("id") String id, com.google.appengine.api.users.User gUser) throws OAuthRequestException {
		userService.ensureProvisioning(gUser);
		meetingService.remove(id, userService.getUser(gUser));
	}
	
}