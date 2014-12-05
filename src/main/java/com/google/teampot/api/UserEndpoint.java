package com.google.teampot.api;

import java.io.IOException;
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
import com.google.api.server.spi.config.Nullable;
import com.google.api.server.spi.response.UnauthorizedException;
import com.google.appengine.api.oauth.OAuthRequestException;

public class UserEndpoint extends BaseEndpoint {

	private static UserDAO dao = new UserDAO();	
	private static UserService userService = UserService.getInstance();
	
	@ApiMethod(
		name = "user.list",
		path = "user",
		httpMethod = HttpMethod.GET
	)
	public List<User> list(@Named("project") @Nullable String projectId, @Named("q") @Nullable String q, com.google.appengine.api.users.User gUser) throws OAuthRequestException, UnauthorizedException {
		userService.ensureEnabled(gUser);
		
		if (projectId != null && q == null) {
			Project project = ProjectService.getInstance().get(projectId);
			Ref2EntityTransformer<User> t = new Ref2EntityTransformer<User>();
			return t.transformTo(project.getUsers());
		} else if (projectId == null && q != null) {
			return q.equals("") ? UserService.getInstance().list(3) : UserService.getInstance().search(q,3);
		} else {
			return UserService.getInstance().list();
		}
	}
	
	@ApiMethod(
		name = "user.get", 
		path = "user/{id}",
		httpMethod = HttpMethod.GET
	)
	public User get(@Named("id") String id, com.google.appengine.api.users.User gUser) throws EntityNotFoundException,OAuthRequestException, UnauthorizedException {
		userService.ensureEnabled(gUser);
		
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
		name = "user.auth", 
		path = "user/auth",
		httpMethod = HttpMethod.POST
	)
	public User auth(@Named("code") String code, com.google.appengine.api.users.User gUser) throws OAuthRequestException, IOException, UnauthorizedException {
		if (gUser == null) throw new UnauthorizedException("User not logged in");
		
		GoogleCredential credential = GoogleServices.getCredentialFromOneTimeCode(gUser.getEmail(), code);
		
		User user = UserService.getInstance().getUser(gUser.getEmail());
		user.setTokens(credential);
				
		UserService.getInstance().provisionProfile(user);
		
		userService.ensureEnabled(user);
		
		return user;
	}
	
	@ApiMethod(
		name = "user.save",
		path = "user",
		httpMethod = HttpMethod.POST
	)
	public User save(User entity, com.google.appengine.api.users.User gUser) throws OAuthRequestException, UnauthorizedException {
		userService.ensureEnabled(gUser);
		
		dao.save(entity);
		return entity;
	}
	
	@ApiMethod(
		name = "user.remove",
		path = "user/{id}",
		httpMethod = HttpMethod.DELETE
	)
	public void remove(@Named("id") String id, com.google.appengine.api.users.User gUser) throws OAuthRequestException, UnauthorizedException {		
		userService.ensureEnabled(gUser);
		
		dao.remove(id);
	}
	
	
	
}