package com.google.teampot.servlet;

import java.io.IOException;
import java.util.Properties;
import java.util.logging.Logger;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MailHandlerServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	
	private static final Logger log = Logger.getLogger(MailHandlerServlet.class.getSimpleName());

	@Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException { 
        Session session = Session.getDefaultInstance(new Properties(), null); 
        try {
			MimeMessage message = new MimeMessage(session, req.getInputStream());

			log.info("Received mail message");
			
			// TODO: implement!
			
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

}
