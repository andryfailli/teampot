package com.google.teampot.servlet.task;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.teampot.Config;
import com.google.teampot.service.AnalyticsService;

public class UpdateAnalyticsServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	
	private static final Logger logger = Logger.getLogger(UpdateAnalyticsServlet.class.getSimpleName());

	@Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException { 
		if (Config.get(Config.FEATURE_ANALYTICS).equals(Config.VALUE_TRUE))
			AnalyticsService.getInstance().updateAnalytics();
	}
	

}
