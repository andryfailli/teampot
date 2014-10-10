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
		path = "user/{id}",
		httpMethod = HttpMethod.GET
	)
	public User get(@Named("id") String id) throws EntityNotFoundException {
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
	public User save(User entity) {
		dao.save(entity);
		return entity;
	}
	
	@ApiMethod(
		name = "user.remove",
		path = "user/{id}",
		httpMethod = HttpMethod.DELETE
	)
	public void remove(@Named("id") String id) {
		dao.remove(id);
	}
	
}