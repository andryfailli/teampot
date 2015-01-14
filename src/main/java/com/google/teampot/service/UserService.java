package com.google.teampot.service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.server.spi.response.BadRequestException;
import com.google.api.server.spi.response.UnauthorizedException;
import com.google.api.services.admin.directory.Directory;
import com.google.api.services.admin.directory.model.Member;
import com.google.api.services.admin.directory.model.Members;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.appengine.api.taskqueue.TaskOptions.Method;
import com.google.appengine.api.utils.SystemProperty;
import com.google.teampot.Config;
import com.google.teampot.GoogleServices;
import com.google.teampot.api.API;
import com.google.teampot.api.exception.GUserNullUnauthorizedException;
import com.google.teampot.dao.UserDAO;
import com.google.teampot.model.User;
import com.google.teampot.util.AppHelper;

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
	
	public List<User> list(){
		return dao.list();
	}
	
	public List<User> list(int limit){
		return dao.list(limit);
	}
	
	public List<User> search(String q, int limit){
		return dao.search(q,limit);
	}
	
	public User get(String key){
		return dao.get(key);
	}
	
	public User getUser(com.google.appengine.api.users.User gUser) {
		return gUser != null ? this.getUser(gUser.getEmail()) : null;
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
	
	public void ensureEnabled(com.google.appengine.api.users.User gUser) throws UnauthorizedException {
		this.ensureEnabled(this.getUser(gUser));
	}
	public void ensureEnabled(String userEmail) throws UnauthorizedException {
		if (userEmail == null) throw new GUserNullUnauthorizedException("User not logged in");
		User user = this.getUser(userEmail);
		this.ensureEnabled(user);
	}
	public void ensureEnabled(User user) throws UnauthorizedException {
		if (user == null) throw new GUserNullUnauthorizedException("User not logged in");
		if (!user.isEnabled())  throw new UnauthorizedException("User not authorized");
	}
	
	public boolean isUserEnabled(User user) {
		String APPS_GROUP = Config.get(Config.APPS_GROUP);
		if (APPS_GROUP != null && !APPS_GROUP.equals("")) {
			
			try {
				Directory directoryService = GoogleServices.getDirectoryServiceDomainWide();
				Member member = directoryService.members().get(APPS_GROUP, user.getEmail()).execute();
				if (member != null) {
					return true;
				} else {
					return false;
				}
			} catch (GoogleJsonResponseException e) {
				if (e.getStatusCode() == 404) {
					return false;
				} else {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return false;
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}
			
		} else {
			return true;
		}
	}
	
	public void ensureAdmin(User user) throws UnauthorizedException {
		
		if (user == null) throw new UnauthorizedException("User not authorized");
		
		com.google.api.services.admin.directory.model.User directoryUser = null;
		try {
			Directory directoryService = GoogleServices.getDirectoryServiceDomainWide();
			directoryUser = directoryService.users().get(user.getEmail()).execute();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new UnauthorizedException("User not authorized");
		}
		
		if (!directoryUser.getIsAdmin()) {
			throw new UnauthorizedException("User not authorized");
		}
		
	}
	
	public void provisionProfile(User actor, User userToBeProvisioned) {

	com.google.api.services.admin.directory.model.User directoryUser = null;
		try {
			Directory directoryService = actor.hasLoggedIn() ? GoogleServices.getDirectoryService(actor) : GoogleServices.getDirectoryServiceDomainWide();
			directoryUser = directoryService.users().get(userToBeProvisioned.getEmail()).set("viewType","domain_public").execute();
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
		
		// enabled?
		userToBeProvisioned.setEnabled(this.isUserEnabled(userToBeProvisioned));
		
		dao.save(userToBeProvisioned);
		
		// spoon task to provision user profile
		/* --- REMOVEME: user profile provisioning is now done from cron
		Queue queue = QueueFactory.getDefaultQueue();
	    TaskOptions task = TaskOptions.Builder
	    	.withUrl(AppHelper.getTaskBaseUrl()+"/provisionUserProfile")
	    	.countdownMillis(86400000) // 1day
	    	.param("user", userToBeProvisioned.getKey())
	    	.method(Method.POST)
	    ; 
        queue.add(task);
        */
	}
	
	public void provisionProfile(User userToBeProvisioned) {
		this.provisionProfile(userToBeProvisioned,userToBeProvisioned);
	}
	
	public void provisionGroup(String groupEmail) {
		
				
		Members directoryUsers = null;
		try {
			Directory directoryService = GoogleServices.getDirectoryServiceDomainWide();
			
			String pageToken = null;
			
			do {
				
				directoryUsers = directoryService.members().list(groupEmail).execute();
				for (Member directoryUser : directoryUsers.getMembers()) {
					
					User user = this.getUser(directoryUser.getEmail());
					this.provisionProfile(user);
					
				}
				
				pageToken = directoryUsers.getNextPageToken();
				
			} while (pageToken != null);
			
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (GeneralSecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
