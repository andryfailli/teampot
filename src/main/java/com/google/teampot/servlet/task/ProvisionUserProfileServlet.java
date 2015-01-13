package com.google.teampot.servlet.task;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.teampot.model.User;
import com.google.teampot.service.UserService;

public class ProvisionUserProfileServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	
	private static final Logger logger = Logger.getLogger(ProvisionUserProfileServlet.class.getSimpleName());

	@Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException { 
		String userKey = req.getParameter("user");
		User user = UserService.getInstance().get(userKey);
		UserService.getInstance().provisionProfile(user);
	}
	

}
