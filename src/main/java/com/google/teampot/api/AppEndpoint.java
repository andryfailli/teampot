package com.google.teampot.api;

import java.io.IOException;
import java.security.GeneralSecurityException;

import com.google.teampot.Config;
import com.google.teampot.api.BaseEndpoint;
import com.google.teampot.model.MeetingActivityEvent;
import com.google.teampot.model.MemberActivityEvent;
import com.google.teampot.model.ProjectActivityEvent;
import com.google.teampot.model.TaskActivityEvent;
import com.google.teampot.model.User;
import com.google.teampot.service.AnalyticsService;
import com.google.teampot.service.UserService;
import com.google.teampot.tablerow.MeetingActivityEventTableRowWriter;
import com.google.teampot.tablerow.MemberActivityEventTableRowWriter;
import com.google.teampot.tablerow.ProjectActivityEventTableRowWriter;
import com.google.teampot.tablerow.TaskActivityEventTableRowWriter;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiMethod.HttpMethod;
import com.google.api.server.spi.response.UnauthorizedException;
import com.google.appengine.api.oauth.OAuthRequestException;

public class AppEndpoint extends BaseEndpoint {

	private static UserService userService = UserService.getInstance();
	
	@ApiMethod(
		name = "app.init",
		path = "app",
		httpMethod = HttpMethod.POST
	)
	public void init(com.google.appengine.api.users.User gUser) throws OAuthRequestException, UnauthorizedException, GeneralSecurityException, IOException {
		User actor = userService.getUser(gUser);
		
		userService.ensureAdmin(actor);
				
		// app group provisioning
		this.provisionAppGroup();
		
		// BQ init
		this.createBigQueryTables();
		
	}
	
	private void provisionAppGroup() {
		String appGroup = Config.get(Config.APPS_GROUP);
		if (appGroup == null || appGroup.equals("")) return;
		userService.provisionGroup(appGroup);		
	}
	
	private void createBigQueryTables() throws GeneralSecurityException, IOException {
		AnalyticsService analyticsService = AnalyticsService.getInstance();
		String dataSetName = "teampot";
		
		analyticsService.createDataset(dataSetName);
		
		analyticsService.createTable(dataSetName, ProjectActivityEvent.class.getSimpleName(), ProjectActivityEventTableRowWriter.getTableSchema());
		analyticsService.createTable(dataSetName, TaskActivityEvent.class.getSimpleName(), TaskActivityEventTableRowWriter.getTableSchema());
		analyticsService.createTable(dataSetName, MeetingActivityEvent.class.getSimpleName(), MeetingActivityEventTableRowWriter.getTableSchema());
		analyticsService.createTable(dataSetName, MemberActivityEvent.class.getSimpleName(), MemberActivityEventTableRowWriter.getTableSchema());
		
	}
	

	
}