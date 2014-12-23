package com.google.teampot.servlet;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.mail.Header;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.teampot.service.mailhandler.MailHandlerService;

public class MailHandlerServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	
	private static final Logger logger = Logger.getLogger(MailHandlerServlet.class.getSimpleName());

	@Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException { 
		logger.info("Received mail message");
        Session session = Session.getDefaultInstance(new Properties(), null); 
        try {
        	logger.info("Trying to parse mail message");
			MimeMessage message = new MimeMessage(session, req.getInputStream());
			
			MailHandlerService.getInstance().processMessage(message);
			
			logger.info("Mail message parsed");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.log(Level.SEVERE,"Exception thrown while parsing mail message",e);
		}
	}
	

}
