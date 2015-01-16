package com.google.teampot.servlet.task;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.teampot.Config;
import com.google.teampot.model.User;
import com.google.teampot.service.UserService;

public class ProvisionGroupServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	
	private static final Logger logger = Logger.getLogger(ProvisionGroupServlet.class.getSimpleName());

	@Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException { 
		String appGroup = Config.get(Config.APPS_GROUP);
		if (appGroup == null || appGroup.equals("")) return;
		UserService.getInstance().provisionGroup(appGroup);		
	}
	

}
