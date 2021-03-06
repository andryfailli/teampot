package com.google.teampot.dao;

import static com.google.teampot.OfyService.ofy;

import java.util.List;

import com.google.teampot.model.Project;
import com.google.teampot.model.User;

public class ProjectDAO extends BaseEntityDAO<Project>{
	
	public ProjectDAO() {
		super(Project.class);
	}
	
	public List<Project> listForUser(User user) {
		return ofy().load().type(Project.class).filter("users",user).list();
	}
	
	public boolean existsWithName(String machineName){
		return ofy().load().type(Project.class).filter("machineName",machineName).keys().list().size()>0;
	}
	
	public Project getByName(String machineName){
		List<Project> projects = ofy().load().type(Project.class).filter("machineName",machineName).limit(1).list();
		return projects.size()>0 ? projects.get(0) : null;
	}

}
