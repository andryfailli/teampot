package com.google.teampot.api;

import java.io.IOException;
import java.util.List;

import com.google.teampot.GoogleServices;
import com.google.teampot.api.exception.EntityNotFoundException;
import com.google.teampot.dao.UserDAO;
import com.google.teampot.model.User;
import com.google.teampot.service.UserService;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiMethod.HttpMethod;
import com.google.api.server.spi.config.Named;
import com.google.appengine.api.oauth.OAuthRequestException;

public class UserEndpoint extends BaseEndpoint {

	private static UserDAO dao = new UserDAO();
	
	@ApiMethod(
		name = "user.list",
		path = "user",
		httpMethod = HttpMethod.GET
	)
	public List<User> list(com.google.appengine.api.users.User gUser) throws OAuthRequestException {
		UserService.getInstance().ensureProvisioning(gUser);
		
		return dao.list();
	}
	
	@ApiMethod(
		name = "user.get", 
		path = "user/{id}",
		httpMethod = HttpMethod.GET
	)
	public User get(@Named("id") String id, com.google.appengine.api.users.User gUser) throws EntityNotFoundException,OAuthRequestException {
		UserService.getInstance().ensureProvisioning(gUser);
		
		User entity = dao.get(id);
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
		UserService.getInstance().ensureProvisioning(gUser);
		
		dao.save(entity);
		return entity;
	}
	
	@ApiMethod(
		name = "user.remove",
		path = "user/{id}",
		httpMethod = HttpMethod.DELETE
	)
	public void remove(@Named("id") String id, com.google.appengine.api.users.User gUser) throws OAuthRequestException {
		UserService.getInstance().ensureProvisioning(gUser);
		
		dao.remove(id);
	}
	
	@ApiMethod(
		name = "user.auth", 
		path = "user/auth",
		httpMethod = HttpMethod.POST
	)
	public void auth(@Named("code") String code, com.google.appengine.api.users.User gUser) throws OAuthRequestException, IOException {
		UserService.getInstance().ensureProvisioning(gUser);
		
		GoogleCredential credential = GoogleServices.getCredentialFromOneTimeCode(gUser.getEmail(), code);
		User user = UserService.getInstance().getUser(gUser);
		user.setTokens(credential);
		dao.save(user);
	}
	
}