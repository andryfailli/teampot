package com.google.teampot.service.mailhandler;

import org.apache.commons.mail.util.MimeMessageParser;

import com.google.teampot.model.Project;
import com.google.teampot.model.User;

public abstract class MailHandlerSubscriber {

	public MailHandlerSubscriber() {
		
	}
	
	public abstract void processMessage(MimeMessageParser message, Project project, User actor) throws Exception;
	
}
