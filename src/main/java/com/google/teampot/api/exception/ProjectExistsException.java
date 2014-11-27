package com.google.teampot.api.exception;

import com.google.api.server.spi.response.BadRequestException;

public class ProjectExistsException extends BadRequestException {

	private static final long serialVersionUID = 1L;

	public ProjectExistsException(String machineName) {
		super("A project with machineName "+machineName+" already exists.");
	}

}
