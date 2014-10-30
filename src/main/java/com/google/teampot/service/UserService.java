package com.google.teampot.service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import com.google.api.services.plus.model.Person;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.appengine.api.taskqueue.TaskOptions.Method;
import com.google.teampot.GoogleServices;
import com.google.teampot.api.API;
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
	
	public User get(String key){
		return dao.get(key);
	}
	
	public User getUser(com.google.appengine.api.users.User gUser) {
		User user;
		if (this.isUserProvisioned(gUser)) {
			user = dao.getByEmail(gUser.getEmail());
		} else {
			user = new User(gUser.getEmail());
			dao.save(user);
		}
		return user;
	}
	
	public boolean isUserProvisioned(com.google.appengine.api.users.User gUser) {
		if (gUser != null) {
			User user = dao.getByEmail(gUser.getEmail());
			return user != null;
		} else return false;		
	}
	
	public void ensureProvisioning(com.google.appengine.api.users.User gUser) {
		this.getUser(gUser);
	}
	
	public void provisionProfile(User user) {

		Person person = null;
		try {
			person = GoogleServices.getPlusService(user).people().get("me").execute();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (GeneralSecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		user.setFirstName(person.getName().getGivenName());
		user.setLastName(person.getName().getFamilyName());
		user.setIconUrl(person.getImage().getUrl());
		dao.save(user);
		
		// spoon task to provision user profile
		Queue queue = QueueFactory.getDefaultQueue();
	    TaskOptions task = TaskOptions.Builder
	    	.withUrl(API.getBaseUrlWithoutHostAndSchema()+"/gae/task/provisionUserProfile")
	    	.countdownMillis(86400000) // 1day
	    	.param("user", user.getKey())
	    	.method(Method.POST)
	    ; 
        queue.add(task);
	}

}
