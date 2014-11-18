package com.google.teampot.api;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.teampot.GoogleServices;
import com.google.teampot.dao.UserDAO;
import com.google.teampot.api.BaseEndpoint;
import com.google.teampot.api.exception.EntityNotFoundException;
import com.google.teampot.model.Project;
import com.google.teampot.model.User;
import com.google.teampot.service.ProjectService;
import com.google.teampot.service.UserService;
import com.google.teampot.transformer.Ref2EntityTransformer;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiMethod.HttpMethod;
import com.google.api.server.spi.config.Named;
import com.google.appengine.api.oauth.OAuthRequestException;
import com.googlecode.objectify.Ref;

public class UserEndpoint extends BaseEndpoint {

	private static UserDAO dao = new UserDAO();
	
	@ApiMethod(
		name = "user.list",
		path = "user",
		httpMethod = HttpMethod.GET
	)
	public List<User> list(@Named("project") String projectId, com.google.appengine.api.users.User gUser) throws OAuthRequestException {
		Project project = ProjectService.getInstance().get(projectId);
		Ref2EntityTransformer<User> t = new Ref2EntityTransformer<User>();
		return t.transformTo(project.getUsers());
	}
	
	@ApiMethod(
		name = "user.get", 
		path = "user/{id}",
		httpMethod = HttpMethod.GET
	)
	public User get(@Named("id") String id, com.google.appengine.api.users.User gUser) throws EntityNotFoundException,OAuthRequestException {
		User entity;
		if (id.equals("me")) {
			entity = UserService.getInstance().getUser(gUser);
		} else {
			entity = dao.get(id);
		}
		if (entity != null)
			return entity;
		else
			throw new EntityNotFoundException(id);
	}
	
	@ApiMethod(
		name = "user.save",
		path = "user",
		httpMethod = HttpMethod.POST
	)
	public User save(User entity, com.google.appengine.api.users.User gUser) throws OAuthRequestException {
		dao.save(entity);
		return entity;
	}
	
	@ApiMethod(
		name = "user.remove",
		path = "user/{id}",
		httpMethod = HttpMethod.DELETE
	)
	public void remove(@Named("id") String id, com.google.appengine.api.users.User gUser) throws OAuthRequestException {		
		dao.remove(id);
	}
	
	@ApiMethod(
		name = "user.auth", 
		path = "user/auth",
		httpMethod = HttpMethod.POST
	)
	public void auth(@Named("code") String code, com.google.appengine.api.users.User gUser) throws OAuthRequestException, IOException {
		GoogleCredential credential = GoogleServices.getCredentialFromOneTimeCode(gUser.getEmail(), code);
		User user = UserService.getInstance().getUser(gUser);
		user.setTokens(credential);
		dao.save(user);
		
		UserService.getInstance().provisionProfile(user);
	}
	
}