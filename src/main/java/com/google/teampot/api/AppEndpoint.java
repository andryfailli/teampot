package com.google.teampot.api;

import com.google.teampot.Config;
import com.google.teampot.api.BaseEndpoint;
import com.google.teampot.model.User;
import com.google.teampot.service.UserService;
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
	public void init(com.google.appengine.api.users.User gUser) throws OAuthRequestException, UnauthorizedException {
		User actor = userService.getUser(gUser);
		
		userService.ensureAdmin(actor);
		
		
		// app group provisioning
		String appGroup = Config.get(Config.APPS_GROUP);
		if (appGroup == null || appGroup.equals("")) return;
		userService.provisionGroup(appGroup, actor);
		
	}
	

	
}