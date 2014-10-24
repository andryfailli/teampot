package com.google.teampot.service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.api.services.admin.directory.Directory;
import com.google.api.services.admin.directory.model.Group;
import com.google.api.services.admin.directory.model.Member;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.Permission;
import com.google.teampot.Config;
import com.google.teampot.GoogleServices;
import com.google.teampot.dao.ProjectDAO;
import com.google.teampot.diff.visitor.EntityDiffVisitor;
import com.google.teampot.model.EntityDiff;
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
	}

}
