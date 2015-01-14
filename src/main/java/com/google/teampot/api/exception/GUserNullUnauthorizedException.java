package com.google.teampot.api.exception;

import java.util.logging.Logger;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.response.UnauthorizedException;
import com.google.teampot.api.BaseEndpoint;

public class GUserNullUnauthorizedException extends UnauthorizedException {

	private static final long serialVersionUID = 1L;

	private static final Logger logger = Logger.getLogger(GUserNullUnauthorizedException.class.getSimpleName());
	
	public GUserNullUnauthorizedException(String message) {
		super(message);
		
		Api apiAnnotation = BaseEndpoint.class.getAnnotation(Api.class);
		
		String clientIdList = "";
		for (int i = 0; i < apiAnnotation.clientIds().length; i++) {
			clientIdList += apiAnnotation.clientIds()[i]+"\r\n";
		}
		logger.info("gUser is NULL. Allowed clientIds: \r\n"+clientIdList);
	}

}
