package com.google.teampot.service.mailhandler;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.logging.Logger;

import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.commons.mail.util.MimeMessageParser;

import com.google.teampot.model.Project;
import com.google.teampot.model.User;
import com.google.teampot.service.ProjectService;
import com.google.teampot.service.UserService;
import com.google.teampot.servlet.MailHandlerServlet;
import com.googlecode.objectify.Ref;

public class MailHandlerService {

	private static MailHandlerService instance;
	
	private static final Logger logger = Logger.getLogger(MailHandlerService.class.getSimpleName());
	
	private Set<MailHandlerSubscriber> subscribers;
	
	private MailHandlerService() {
		this.subscribers = new LinkedHashSet<MailHandlerSubscriber>();
		this.subscribe(new MailHandlerTaskMailSubscriber());
	}
	
	public static MailHandlerService getInstance() {
		if (instance == null) instance = new MailHandlerService();
		return instance;
	}
	
	
	public void processMessage(MimeMessage mimeMessage) throws Exception {
		
		MimeMessageParser message = new MimeMessageParser(mimeMessage);
		message.parse();
		
		String projectId = message.getTo().get(0).toString().split("@")[0];
		Project project = ProjectService.getInstance().getByName(projectId);
		
		if (project == null) {
			logger.info("Project with machine name "+projectId+" does not exists. Ignoring email.");
			return;
		}
		
		String from = message.getFrom().toString();
		User actor = UserService.getInstance().getUser(from);
		if (actor == null) {
			logger.info("User "+from+" does not exists. Ignoring email.");
			return;
		}
		if (!project.getUsers().contains(Ref.create(actor))) {
			logger.info("User "+actor.getEmail()+" is not in "+projectId+"'s team. Ignoring email.");
			return;
		}
		
		// finally
		for (MailHandlerSubscriber subscriber : subscribers) {
			subscriber.processMessage(message,project,actor);
		}
		
	}
	
	public void subscribe(MailHandlerSubscriber subscriber) {
		this.subscribers.add(subscriber);
	}
	
}
