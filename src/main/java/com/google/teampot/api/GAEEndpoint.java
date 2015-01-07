package com.google.teampot.api;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import com.google.teampot.GoogleServices;
import com.google.teampot.model.Meeting;
import com.google.teampot.model.Project;
import com.google.teampot.model.User;
import com.google.teampot.service.AnalyticsService;
import com.google.teampot.service.MeetingService;
import com.google.teampot.service.ProjectService;
import com.google.teampot.service.UserService;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiMethod.HttpMethod;
import com.google.api.server.spi.config.Named;
import com.google.api.services.drive.model.Change;

public class GAEEndpoint extends BaseEndpoint {
	
	private static Logger logger = Logger.getLogger(GAEEndpoint.class.getSimpleName());

	@ApiMethod(
		name = "gae.webhook_receiveFolderChanges", 
		path = "gae/webhook/receiveFolderChanges",
		httpMethod = HttpMethod.POST
	)
	public void webhook_receiveFolderChanges(HttpServletRequest httpServletRequest, Change change) {
		String projectKey = httpServletRequest.getHeader("X-Goog-Channel-Token");
		Project project = ProjectService.getInstance().get(projectKey);
		logger.info("Receiving folder changes for project "+projectKey);
		//TODO: to be implemented
	}
	
	@ApiMethod(
		name = "gae.task_watchFolderChanges", 
		path = "gae/task/watchFolderChanges",
		httpMethod = HttpMethod.POST
	)
	public void task_watchFolderChanges(@Named("project") String projectKey) throws IOException, GeneralSecurityException {
		Project project = ProjectService.getInstance().get(projectKey);
		ProjectService.getInstance().watchFolderChanges(GoogleServices.getDriveService(project.getOwner().get()), project);
	}
	
	@ApiMethod(
		name = "gae.task_provisionUserProfile", 
		path = "gae/task/provisionUserProfile",
		httpMethod = HttpMethod.POST
	)
	public void task_provisionUserProfile(@Named("user") String userKey) throws IOException, GeneralSecurityException {
		User user = UserService.getInstance().get(userKey);
		UserService.getInstance().provisionProfile(user);
	}
	
	@ApiMethod(
		name = "gae.task_pollEnd", 
		path = "gae/task/pollEnd",
		httpMethod = HttpMethod.POST
	)
	public void task_pollEnd(@Named("meeting") String meetingKey) throws IOException, GeneralSecurityException {
		Meeting meeting = MeetingService.getInstance().get(meetingKey);
		MeetingService.getInstance().pollEnd(meeting);
	}
	
	@ApiMethod(
		name = "gae.cron_updateAnalytics", 
		path = "gae/cron/updateAnalytics",
		httpMethod = HttpMethod.GET
	)
	public void cron_updateAnalytics() {
		AnalyticsService.getInstance().updateAnalytics();
	}

}