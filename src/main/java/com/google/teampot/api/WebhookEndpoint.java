package com.google.teampot.api;

import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import com.google.teampot.model.Project;
import com.google.teampot.service.ProjectService;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiMethod.HttpMethod;
import com.google.api.services.drive.model.Change;

public class WebhookEndpoint extends BaseEndpoint {
	
	private static Logger logger = Logger.getLogger(WebhookEndpoint.class.getSimpleName());

	@ApiMethod(
		name = "webhook.receiveFolderChanges", 
		path = "webhook/receiveFolderChanges",
		httpMethod = HttpMethod.POST
	)
	public void webhook_receiveFolderChanges(HttpServletRequest httpServletRequest, Change change) {
		String projectKey = httpServletRequest.getHeader("X-Goog-Channel-Token");
		Project project = ProjectService.getInstance().get(projectKey);
		logger.info("Receiving folder changes for project "+projectKey);
		//TODO: to be implemented
	}

}