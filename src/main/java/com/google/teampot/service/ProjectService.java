package com.google.teampot.service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.mail.MessagingException;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.admin.directory.Directory;
import com.google.api.services.admin.directory.model.Group;
import com.google.api.services.admin.directory.model.Member;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.Channel;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.Permission;
import com.google.api.services.groupssettings.Groupssettings;
import com.google.api.services.groupssettings.model.Groups;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.appengine.api.taskqueue.TaskOptions.Method;
import com.google.teampot.Config;
import com.google.teampot.GoogleServices;
import com.google.teampot.api.API;
import com.google.teampot.api.exception.ProjectExistsException;
import com.google.teampot.dao.ProjectDAO;
import com.google.teampot.diff.visitor.EntityDiffVisitor;
import com.google.teampot.model.EntityDiff;
import com.google.teampot.model.MemberActivityEvent;
import com.google.teampot.model.MemberActivityEventVerb;
import com.google.teampot.model.Project;
import com.google.teampot.model.ProjectActivityEvent;
import com.google.teampot.model.EntityActivityEventVerb;
import com.google.teampot.model.User;
import com.google.teampot.util.AppHelper;

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
	
	public void save(Project entity) throws ProjectExistsException{
		this.save(entity,null);
	}
	
	public void save(Project entity, User actor) throws ProjectExistsException{
		ProjectActivityEvent activtyEvent = new ProjectActivityEvent();
		if (entity.getId() == null) {
			
			if (dao.existsWithName(entity.getMachineName())) {
				throw new ProjectExistsException(entity.getMachineName());
			} else {
				activtyEvent.setVerb(EntityActivityEventVerb.CREATE);	
				this.initProject(entity, actor);
			}
			
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
		Groupssettings groupssettingsService = null;
		try {
			
			directoryService = GoogleServices.getDirectoryServiceDomainWide();
			driveService = GoogleServices.getDriveService(user);
			groupssettingsService = GoogleServices.getGroupssettingsDomainWide();
			
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
		
		// group email
		String groupEmail = project.getMachineName()+"@"+Config.get(Config.APPS_DOMAIN);
		project.setGroupEmail(groupEmail);
		
		// create a new Google Group or use the existing one		
		Group group = null;
		boolean isNew = true;
		try {
			group = directoryService.groups().get(groupEmail).execute();
			isNew = false;
		} catch (GoogleJsonResponseException e) {
			e.printStackTrace();
			if (e.getStatusCode() == 404) {
				// group does not exists
				group = new Group();
				group.setEmail(groupEmail);
				isNew = true;
			} else {
				e.printStackTrace();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		group.setName(project.getName());
		group.setDescription(project.getDescription());
		
		try {
			if (isNew)
				group = directoryService.groups().insert(group).execute();
			else
				group = directoryService.groups().update(groupEmail,group).execute();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// set Google Group settings
		Groups groupssettings = new Groups();
		groupssettings.setIsArchived("true");
		try {
			groupssettingsService.groups().update(groupEmail, groupssettings).execute();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		// add project owner in group & add app email in group
		Member member = new Member();
		member.setEmail(user.getEmail());
		member.setRole("OWNER");
		Member appMember = new Member();
		appMember.setEmail(user.getEmail());
		appMember.setRole("OWNER");
		try {
			member = directoryService.members().insert(group.getId(), member).execute();
			member = directoryService.members().insert(group.getId(), appMember).execute();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		// create & set project Drive folder
		File folder = new File();
		folder.setTitle(project.getName());
		folder.setMimeType("application/vnd.google-apps.folder");
		try {
			folder = driveService.files().insert(folder).execute();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		project.setFolder(folder.getId());
		
		dao.save(project);
		
		// share Drive folder
		Permission permission = new Permission();
		permission.setType("group");
		permission.setRole("writer");
		permission.setValue(group.getEmail());
		
		try {
			permission = driveService.permissions().insert(folder.getId(), permission).execute();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		// subscribe to Drive folder changes
		// TODO: redesign drive watch feature...
		//this.watchFolderChanges(driveService,project);
		
		
		// finally, notify
		try {
			this.sendProjectInitNotification(project,user);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MessagingException e) {
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
	
	public void addMember(Project project, User member, User actor) {
		project.addUser(member);
		dao.save(project);
		
		Directory directoryService = null;
		try {
			directoryService = GoogleServices.getDirectoryService(actor);
			
		} catch (GeneralSecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// add user in group
		Member groupMember = new Member();
		groupMember.setEmail(member.getEmail());
		groupMember.setRole("OWNER");
		try {
			groupMember = directoryService.members().insert(project.getMachineName()+"@"+Config.get(Config.APPS_DOMAIN), groupMember).execute();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// Notify member
		try {
			this.sendAddMemberNotification(project, member, actor);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		MemberActivityEvent activityEvent = new MemberActivityEvent(project, actor);
		activityEvent.setUser(member);
		activityEvent.setVerb(MemberActivityEventVerb.ADD);
		
		ActivityEventService.getInstance().registerActivityEvent(activityEvent);
	}
	
	public void removeMember(Project project, User member, User actor) {
		project.removeUser(member);
		dao.save(project);
		
		Directory directoryService = null;
		try {
			directoryService = GoogleServices.getDirectoryService(actor);
			
		} catch (GeneralSecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// remove user from group
		try {
			directoryService.members().delete(project.getMachineName()+"@"+Config.get(Config.APPS_DOMAIN), member.getEmail()).execute();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		// Notify member
		try {
			this.sendRemoveMemberNotification(project, member, actor);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		MemberActivityEvent activityEvent = new MemberActivityEvent(project, actor);
		activityEvent.setUser(member);
		activityEvent.setVerb(MemberActivityEventVerb.REMOVE);
		
		ActivityEventService.getInstance().registerActivityEvent(activityEvent);
	}
	
	private void sendProjectInitNotification(Project project, User actor) throws UnsupportedEncodingException, MessagingException {
		
		String actionUrl = AppHelper.getBaseUrl()+"/#/project/"+project.getKey();
		
		Map<String, Object> data = new LinkedHashMap<String, Object>();
		data.put("header","Project "+project.getName());
		data.put("body",actor.getFirstName()+" created a project");
		data.put("actorPhoto", actor.getIconUrl());
		data.put("actionLabel","Open Project");
		data.put("actionUrl",actionUrl);
		
		String mailHtml = TemplatingService.getInstance().compile(data, "base.html.vm");
		String mailPlaintext = TemplatingService.getInstance().compile(data, "base.txt.vm");
		
		NotificationService.getInstance().sendMessage("Project "+project.getName(), mailPlaintext, mailHtml, project, actor);
		
	}
	
	private void sendAddMemberNotification(Project project, User member, User actor) throws UnsupportedEncodingException, MessagingException {
		
		String actionUrl = AppHelper.getBaseUrl()+"/#/project/"+project.getKey();
		
		Map<String, Object> data = new LinkedHashMap<String, Object>();
		data.put("header","Project "+project.getName());
		data.put("body","Welcome onboard! "+actor.getFirstName()+" added you in the team.");
		data.put("actorPhoto", actor.getIconUrl());
		data.put("actionLabel","Open Project");
		data.put("actionUrl",actionUrl);
		
		String mailHtml = TemplatingService.getInstance().compile(data, "base.html.vm");
		String mailPlaintext = TemplatingService.getInstance().compile(data, "base.txt.vm");
		
		NotificationService.getInstance().sendMessage("Project "+project.getName(), mailPlaintext, mailHtml, member, actor);
		
	}
	
	private void sendRemoveMemberNotification(Project project, User member, User actor) throws UnsupportedEncodingException, MessagingException {
		
		String actionUrl = AppHelper.getBaseUrl();
		
		Map<String, Object> data = new LinkedHashMap<String, Object>();
		data.put("header","Project "+project.getName());
		data.put("body","Sorry to see you leave... "+actor.getFirstName()+" removed you from the team.");
		data.put("actorPhoto", actor.getIconUrl());
		data.put("actionLabel","Open TeamPot");
		data.put("actionUrl",actionUrl);
		
		String mailHtml = TemplatingService.getInstance().compile(data, "base.html.vm");
		String mailPlaintext = TemplatingService.getInstance().compile(data, "base.txt.vm");
		
		NotificationService.getInstance().sendMessage("Project "+project.getName(), mailPlaintext, mailHtml, member, actor);
		
	}
	
}
