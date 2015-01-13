package com.google.teampot.service;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import javax.mail.Address;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.commons.mail.util.MimeMessageParser;

import com.google.teampot.model.Project;
import com.google.teampot.model.User;
import com.google.teampot.service.mailhandler.MailHandlerSubscriber;
import com.google.teampot.service.mailhandler.MailHandlerTaskMailSubscriber;

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
		
		Project project = null;
		
		// find project id
		List<Address> addresses = new ArrayList<Address>();
		addresses.addAll(message.getTo());
		addresses.addAll(message.getCc());
		addresses.addAll(message.getBcc());
		for (Address rawAddress : addresses) {
			InternetAddress address = (InternetAddress) rawAddress;
			String mayBeProjectId = address.getAddress().split("@")[0];
			project = ProjectService.getInstance().getByName(mayBeProjectId);
			if (project != null) {
				break;
			}
		}
		
		if (project == null) {
			logger.info("No target project found. Ignoring email.");
			return;
		}
		
		String[] inReplyToheader = mimeMessage.getHeader("In-Reply-To");
		if (inReplyToheader != null && inReplyToheader.length>0) {
			logger.info("It's a reply because 'In-Reply-To' header found. Ignoring email.");
			return;
		}
		
		String from = InternetAddress.parse(message.getFrom())[0].getAddress();
		
		if (!UserService.getInstance().isUserProvisioned(from)) {
			logger.info("User "+from+" does not exists. Ignoring email.");
			return;
		}
		
		User actor = UserService.getInstance().getUser(from);
		if (!project.hasUser(actor)) {
			logger.info("User "+actor.getEmail()+" is not in "+project.getMachineName()+"'s team. Ignoring email.");
			return;
		}
		
		// finally
		logger.info("Wow, that's an important email. Let's process it...");
		for (MailHandlerSubscriber subscriber : subscribers) {
			subscriber.processMessage(message,project,actor);
		}
		
	}
	
	public void subscribe(MailHandlerSubscriber subscriber) {
		this.subscribers.add(subscriber);
	}
	
}
