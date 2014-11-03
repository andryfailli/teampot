package com.google.teampot.service;

import java.io.IOException;
import java.security.GeneralSecurityException;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.appengine.api.taskqueue.TaskOptions.Method;
import com.google.appengine.api.utils.SystemProperty;
import com.google.teampot.Config;
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
		return this.getUser(gUser.getEmail());
	}
	public User getUser(String userEmail) {
		User user;
		
		if (SystemProperty.environment.value() == SystemProperty.Environment.Value.Development && userEmail.equals("example@example.com")) {
			userEmail = Config.get(Config.DEV_LOCALHOST_USER);
		}
		
		if (this.isUserProvisioned(userEmail)) {
			user = dao.getByEmail(userEmail);
		} else {
			user = new User(userEmail);
			dao.save(user);
		}
		return user;
	}
	
	public boolean isUserProvisioned(com.google.appengine.api.users.User gUser) {
		return this.isUserProvisioned(gUser.getEmail());	
	}
	public boolean isUserProvisioned(String userEmail) {
		if (userEmail != null) {
			User user = dao.getByEmail(userEmail);
			return user != null;
		} else return false;		
	}
	
	public void ensureProvisioning(com.google.appengine.api.users.User gUser) {
		this.getUser(gUser);
	}
	public void ensureProvisioning(String userEmail) {
		this.getUser(userEmail);
	}
	
	public void provisionProfile(User actor, User userToBeProvisioned) {

		com.google.api.services.admin.directory.model.User directoryUser = null;
		try {
			directoryUser = GoogleServices.getDirectoryService(actor).users().get(userToBeProvisioned.getEmail()).execute();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (GeneralSecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		userToBeProvisioned.setFirstName(directoryUser.getName().getGivenName());
		userToBeProvisioned.setLastName(directoryUser.getName().getFamilyName());
		userToBeProvisioned.setIconUrl(directoryUser.getThumbnailPhotoUrl());
		dao.save(userToBeProvisioned);
		
		// spoon task to provision user profile
		Queue queue = QueueFactory.getDefaultQueue();
	    TaskOptions task = TaskOptions.Builder
	    	.withUrl(API.getBaseUrlWithoutHostAndSchema()+"/gae/task/provisionUserProfile")
	    	.countdownMillis(86400000) // 1day
	    	.param("user", userToBeProvisioned.getKey())
	    	.method(Method.POST)
	    ; 
        queue.add(task);
	}
	
	public void provisionProfile(User userToBeProvisioned) {
		this.provisionProfile(userToBeProvisioned,userToBeProvisioned);
	}

}
