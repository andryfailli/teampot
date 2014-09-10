package com.google.teampot.api;

import java.util.ArrayList;
import java.util.List;

import com.google.teampot.model.Project;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiReference;
import com.google.api.server.spi.config.Named;
import com.google.api.server.spi.response.NotFoundException;

@ApiReference(BaseEndpoint.class)
public class ProjectEndpoint extends BaseEndpoint {

	@ApiMethod(name = "project.list")
	public List<Project> list() {
		// TODO: to be implemented
		return new ArrayList<Project>();
	}
	
	@ApiMethod(name = "project.get")
	public Project get(@Named("key") String key) throws NotFoundException {
		//TODO: to be implemented
		return new Project("Dummy project (method to be implemented");
	}

}
