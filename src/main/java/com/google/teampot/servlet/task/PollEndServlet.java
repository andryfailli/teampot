package com.google.teampot.servlet.task;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.teampot.model.Meeting;
import com.google.teampot.service.MeetingService;

public class PollEndServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	
	private static final Logger logger = Logger.getLogger(PollEndServlet.class.getSimpleName());

	@Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException { 
		String meetingKey = req.getParameter("meeting");
		Meeting meeting = MeetingService.getInstance().get(meetingKey);
		MeetingService.getInstance().pollEnd(meeting);
	}
	

}
