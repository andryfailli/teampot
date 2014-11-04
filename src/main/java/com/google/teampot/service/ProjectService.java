package com.google.teampot.service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.api.services.admin.directory.Directory;
import com.google.api.services.admin.directory.model.Group;
import com.google.api.services.admin.directory.model.Member;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.Channel;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.Permission;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.appengine.api.taskqueue.TaskOptions.Method;
import com.google.teampot.Config;
import com.google.teampot.GoogleServices;
import com.google.teampot.api.API;
import com.google.teampot.dao.ProjectDAO;
import com.google.teampot.diff.visitor.EntityDiffVisitor;
import com.google.teampot.model.EntityDiff;
import com.google.teampot.model.MemberActivityEvent;
import com.google.teampot.model.MemberActivityEventVerb;
import com.google.teampot.model.Project;
import com.google.teampot.model.ProjectActivityEvent;
import com.google.teampot.model.EntityActivityEventVerb;
import com.google.teampot.model.User;

import de.danielbechler.diff.ObjectDifferBuilder;
import de.danielbechler.diff.node.DiffNode;

public class ProjectService{

	private static ProjectService instance;
	
	private ProjectDAO dao; 
	private static ActivityEventService activityEventService = ActivityEventService.getInstance();
	
	private ProjectService() {
		this.dao = new ProjectDAO();
	}
	
	public static ProjectService getInstance() {
		if (instance == null) instance = new ProjectService();
		return instance;
	}
	
	public List<Project> list() {
		return dao.list();
	}
	
	public List<Project> listForUser(User user) {
		return dao.listForUser(user);
	}
	
	public Project get(String key){
		return dao.get(key);
	}
	
	public void save(Project entity){
		this.save(entity,null);
	}
	
	public void save(Project entity, User actor){
		ProjectActivityEvent activtyEvent = new ProjectActivityEvent();
		if (entity.getId() == null) {
			activtyEvent.setVerb(EntityActivityEventVerb.CREATE);
			
			this.initProject(entity, actor);
			
		} else {
			
			Project oldEntity = dao.get(entity.getKey());
			
			activtyEvent.setVerb(EntityActivityEventVerb.EDIT);			
			
			DiffNode diffs = ObjectDifferBuilder.buildDefault().compare(entity, oldEntity);
			if (diffs.hasChanges()) {
				Map<String,EntityDiff> entityDiffs = new LinkedHashMap<String,EntityDiff>();
				diffs.visit(new EntityDiffVisitor(entity, oldEntity,entityDiffs));
				activtyEvent.setDiffs(entityDiffs);
			}

		}
		dao.save(entity);
		activtyEvent.setProject(entity);
		activtyEvent.setActor(actor);
		activityEventService.registerActivityEvent(activtyEvent);
	}
	
	public void remove(String key){		
		this.remove(key,null);
	}
	
	public void remove(String key, User actor){
		Project entity = dao.get(key);
		activityEventService.registerActivityEvent(new ProjectActivityEvent(entity, actor, EntityActivityEventVerb.DELETE));
		dao.remove(key);
	}
	
	private void initProject(Project project, User user) {
		
		Directory directoryService = null;
		Drive driveService = null;
		try {
			
			directoryService = GoogleServices.getDirectoryService(user);
			driveService = GoogleServices.getDriveService(user);
			
		} catch (GeneralSecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// set project owner
		project.setOwner(user);
		project.addUser(user);
		
		
		// create a new Google Group
		String groupEmail = project.getMachineName()+"@"+Config.get(Config.APPS_DOMAIN);
		Group group = new Group();
		group.setName(project.getName());
		group.setEmail(groupEmail);
		group.setDescription(project.getDescription());
		
		try {
			group = directoryService.groups().insert(group).execute();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		// add project owner in group
		Member member = new Member();
		member.setEmail(user.getEmail());
		member.setRole("OWNER");
		try {
			member = directoryService.members().insert(group.getId(), member).execute();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		// create & set project Drive folder
		File folder = new File();
		folder.setTitle(project.getName());
		folder.setMimeType("application/vnd.google-apps.folder");
		
		Permission permission = new Permission();
		permission.setType("group");
		permission.setRole("writer");
		permission.setValue(group.getEmail());
		folder.setPermissions(new ArrayList<Permission>(Arrays.asList(permission)));
		
		try {
			folder = driveService.files().insert(folder).execute();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		project.setFolder(folder.getId());
		
		dao.save(project);
		
		// subscribe to Drive folder changes
		try {
	    	this.watchFolderChanges(driveService,project);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				
		
	}
	
	public Channel watchFolderChanges(Drive driveService, Project project) throws IOException {
		
		Channel channel = new Channel();
	    channel.setId(java.util.UUID.randomUUID().toString());
	    channel.setToken(project.getKey());
	    channel.setType("web_hook");
	    channel.setAddress(API.getBaseUrl()+"/gae/webhook/receiveFolderChanges");
	    long expirationMillis = 86400000; // 1day
	    channel.setExpiration( (new Date()).getTime() + expirationMillis);
	    
	    // task for renewing watch channel
	    Queue queue = QueueFactory.getDefaultQueue();
	    TaskOptions task = TaskOptions.Builder
	    	.withUrl(API.getBaseUrlWithoutHostAndSchema()+"/gae/task/watchFolderChanges")
	    	.countdownMillis(expirationMillis)
	    	.param("project", project.getKey())
	    	.param("user", project.getOwner().getKey().getString())
	    	.method(Method.POST)
	    ; 
        queue.add(task);
        
        return driveService.files().watch(project.getFolder(),channel).execute();
	}
	
	public void addUser(Project project, User user, User actor) {
		project.addUser(user);
		dao.save(project);
		
		MemberActivityEvent activityEvent = new MemberActivityEvent(project, actor);
		activityEvent.setUser(user);
		activityEvent.setVerb(MemberActivityEventVerb.ADD);
		
		ActivityEventService.getInstance().registerActivityEvent(activityEvent);
	}

}
