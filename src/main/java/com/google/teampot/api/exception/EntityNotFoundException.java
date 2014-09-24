package com.google.teampot.api.exception;

import com.google.api.server.spi.response.NotFoundException;
import com.googlecode.objectify.Key;

public class EntityNotFoundException extends NotFoundException {

	private static final long serialVersionUID = 1L;

	public EntityNotFoundException(String key) {
		super("Entity with key "+key+" not found.");
	}
	
	public EntityNotFoundException(Key<?> key) {
		this(key.getString());
	}

}
