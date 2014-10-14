package com.google.teampot.api;

import java.util.List;

import com.google.teampot.api.exception.EntityNotFoundException;
import com.google.teampot.dao.ProjectDAO;
import com.google.teampot.model.Project;
import com.google.teampot.service.UserService;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiMethod.HttpMethod;
import com.google.api.server.spi.config.Named;
import com.google.appengine.api.oauth.OAuthRequestException;

public class ProjectEndpoint extends BaseEndpoint{

	private static ProjectDAO dao = new ProjectDAO();
	
	@ApiMethod(
		name = "project.list",
		path = "project",
		httpMethod = HttpMethod.GET
	)
	public List<Project> list(com.google.appengine.api.users.User gUser) throws OAuthRequestException {
		UserService.getInstance().ensureProvisioning(gUser);
		
		return dao.list();
	}
	
	@ApiMethod(
		name = "project.get", 
		path = "project/{id}",
		httpMethod = HttpMethod.GET
	)
	public Project get(@Named("id") String id, com.google.appengine.api.users.User gUser) throws OAuthRequestException,EntityNotFoundException {
		UserService.getInstance().ensureProvisioning(gUser);
		
		Project entity = dao.get(id);
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
		UserService.getInstance().ensureProvisioning(gUser);
		
		if (entity.getId() == null) {
			
			entity.setOwner(UserService.getInstance().getUser(gUser));
			
			//TODO FIXME: create a new Drive folder
			entity.setFolder("0B4_NX57yMnRsMXAwNDZfTDBOcGM");
			
			//TODO FIXME: create a new Google group
			entity.setMachineName("misc-it");
		}
		
		dao.save(entity);
		return entity;
	}
	
	@ApiMethod(
		name = "project.remove",
		path = "project/{id}",
		httpMethod = HttpMethod.DELETE
	)
	public void remove(@Named("id") String id, com.google.appengine.api.users.User gUser) throws OAuthRequestException {
		UserService.getInstance().ensureProvisioning(gUser);
		
		dao.remove(id);
	}
	
}