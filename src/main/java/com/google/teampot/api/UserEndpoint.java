package com.google.teampot.api;

import java.util.List;

import com.google.teampot.api.exception.EntityNotFoundException;
import com.google.teampot.dao.UserDAO;
import com.google.teampot.model.User;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiMethod.HttpMethod;
import com.google.api.server.spi.config.Named;

public class UserEndpoint extends BaseEndpoint {

	private static UserDAO dao = new UserDAO();
	
	@ApiMethod(
		name = "user.list",
		path = "user",
		httpMethod = HttpMethod.GET
	)
	public List<User> list() {
		return dao.list();
	}
	
	@ApiMethod(
		name = "user.get", 
		path = "user/{key}",
		httpMethod = HttpMethod.GET
	)
	public User get(@Named("key") String key) throws EntityNotFoundException {
		User entity = dao.get(key);
		if (entity != null)
			return entity;
		else
			throw new EntityNotFoundException(key);
	}
	
	@ApiMethod(
		name = "user.save",
		path = "user",
		httpMethod = HttpMethod.POST
	)
	public User save(User entity) {
		dao.save(entity);
		return entity;
	}
	
	@ApiMethod(
		name = "user.remove",
		path = "user/{key}",
		httpMethod = HttpMethod.DELETE
	)
	public void remove(@Named("key") String key) {
		dao.remove(key);
	}
	
}