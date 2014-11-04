package com.google.teampot.api;

import java.util.List;

import com.google.teampot.api.exception.EntityNotFoundException;
import com.google.teampot.model.Project;
import com.google.teampot.model.User;
import com.google.teampot.service.ProjectService;
import com.google.teampot.service.UserService;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiMethod.HttpMethod;
import com.google.api.server.spi.config.Named;
import com.google.appengine.api.oauth.OAuthRequestException;

public class ProjectEndpoint extends BaseEndpoint{

	private static ProjectService projectService = ProjectService.getInstance();
	private static UserService userService = UserService.getInstance();
	
	@ApiMethod(
		name = "project.list",
		path = "project",
		httpMethod = HttpMethod.GET
	)
	public List<Project> list(com.google.appengine.api.users.User gUser) throws OAuthRequestException {
		User user = userService.getUser(gUser);
		return projectService.listForUser(user);
	}
	
	@ApiMethod(
		name = "project.get", 
		path = "project/{id}",
		httpMethod = HttpMethod.GET
	)
	public Project get(@Named("id") String id, com.google.appengine.api.users.User gUser) throws OAuthRequestException,EntityNotFoundException {
		Project entity = projectService.get(id);
		if (entity != null)
			return entity;
		else
			throw new EntityNotFoundException(id);
	}
	
	@ApiMethod(
		name = "project.save",
		path = "project",
		httpMethod = HttpMethod.POST
	)
	public Project save(Project entity, com.google.appengine.api.users.User gUser) throws OAuthRequestException {
		projectService.save(entity,userService.getUser(gUser));
		return entity;
	}
	
	@ApiMethod(
		name = "project.remove",
		path = "project/{id}",
		httpMethod = HttpMethod.DELETE
	)
	public void remove(@Named("id") String id, com.google.appengine.api.users.User gUser) throws OAuthRequestException {
		projectService.remove(id);
	}
	
	@ApiMethod(
		name = "project.addMember",
		path = "project/{id}/addmember/{memberEmail}",
		httpMethod = HttpMethod.POST
	)
	public User addMember(@Named("id") String id, @Named("memberEmail") String memberEmail, com.google.appengine.api.users.User gUser) throws OAuthRequestException {
		User member = userService.getUser(memberEmail);
		User user = userService.getUser(gUser);
		Project entity = projectService.get(id);

		projectService.addMember(entity, member, user);
		
		UserService.getInstance().provisionProfile(user, member);
		
		return member;
	}
	
}