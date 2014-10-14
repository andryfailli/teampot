package com.google.teampot.service;

import com.google.teampot.dao.UserDAO;
import com.google.teampot.model.User;

public class UserService {

	private static UserService instance;
	
	private UserDAO dao; 
	
	private UserService() {
		this.dao = new UserDAO();
	}
	
	public static UserService getInstance() {
		if (instance == null) instance = new UserService();
		return instance;
	}
	
	public User getUser(com.google.appengine.api.users.User gUser) {
		User user = dao.getByEmail(gUser.getEmail());
		if (user == null) {
			user = new User(gUser.getEmail());
			dao.save(user);
		}
		return user;
	}
	
	public void ensureProvisioning(com.google.appengine.api.users.User gUser) {
		this.getUser(gUser);
	}

}
