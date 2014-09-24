package com.google.teampot.api;

import java.util.List;

import com.google.teampot.api.exception.EntityNotFoundException;
import com.google.teampot.dao.ActivityEventDAO;
import com.google.teampot.dao.CommentDAO;
import com.google.teampot.dao.MeetingDAO;
import com.google.teampot.dao.ProjectDAO;
import com.google.teampot.dao.TaskDAO;
import com.google.teampot.dao.UserDAO;
import com.google.teampot.model.Meeting;
import com.google.teampot.model.MeetingActivityEvent;
import com.google.teampot.model.Project;
import com.google.teampot.model.Task;
import com.google.teampot.model.TaskActivityEvent;
import com.google.teampot.model.TaskComment;
import com.google.teampot.model.User;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiMethod.HttpMethod;
import com.google.api.server.spi.config.Named;
import com.googlecode.objectify.Ref;

public class ProjectEndpoint extends BaseEndpoint{

	private static ProjectDAO dao = new ProjectDAO();
	
	@ApiMethod(
		name = "project.list",
		path = "project",
		httpMethod = HttpMethod.GET
	)
	public List<Project> list() {
		return dao.list();
	}
	
	@ApiMethod(
		name = "project.get", 
		path = "project/{key}",
		httpMethod = HttpMethod.GET
	)
	public Project get(@Named("key") String key) throws EntityNotFoundException {
		Project entity = dao.get(key);
		if (entity != null)
			return entity;
		else
			throw new EntityNotFoundException(key);
	}
	
	@ApiMethod(
		name = "project.save",
		path = "project",
		httpMethod = HttpMethod.POST
	)
	public Project save(Project entity) {
		dao.save(entity);
		return entity;
	}
	
	@ApiMethod(
		name = "project.remove",
		path = "project/{key}",
		httpMethod = HttpMethod.DELETE
	)
	public void remove(@Named("key") String key) {
		dao.remove(key);
	}
	
}