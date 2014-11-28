package com.google.teampot.service;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import com.google.teampot.Config;
import com.google.teampot.model.Project;
import com.google.teampot.model.User;

public class NotificationService {

	private static NotificationService instance;
	
	private Session session;
	
	private NotificationService() {
		Properties props = new Properties();
        this.session = Session.getDefaultInstance(props, null);
	}
	
	public static NotificationService getInstance() {
		if (instance == null) instance = new NotificationService();
		return instance;
	}
	
	private Message prepareMessage(String subject, User recipient, User sender) throws UnsupportedEncodingException, MessagingException {
		Message msg = new MimeMessage(session);
		msg.setFrom(new InternetAddress(Config.get(Config.SERVICE_ACCOUNT_EMAIL), "TeamPot"));
        if (sender != null) {
        	msg.setReplyTo(new Address[]{new InternetAddress(sender.getEmail(), sender.getFirstName()+" "+sender.getLastName())});
        }
        msg.addRecipient(Message.RecipientType.TO, new InternetAddress(recipient.getEmail(), recipient.getFirstName()+" "+recipient.getLastName()));
        msg.setSubject(subject);
        return msg;
	}
	
	public void sendMessage(String subject, String plainBody, User recipient, User sender) throws UnsupportedEncodingException, MessagingException {
		Message msg = this.prepareMessage(subject, recipient, sender);
		msg.setText(plainBody);
        Transport.send(msg);
	}
	
	public void sendMessage(String subject, String plainBody, String htmlBody, User recipient, User sender) throws UnsupportedEncodingException, MessagingException {
		Message msg = this.prepareMessage(subject, recipient, sender);
		
		msg.setText(plainBody);
		
		Multipart mp = new MimeMultipart();
		
		MimeBodyPart htmlPart = new MimeBodyPart();
		htmlPart.setContent(htmlBody, "text/html");
		mp.addBodyPart(htmlPart);
		
		msg.setContent(mp);
		
        Transport.send(msg);
	}
	
	public void sendProjectInitMessage(Project project, User actor) throws UnsupportedEncodingException, MessagingException {
		
		//TODO: set correct url
		String actionUrl = "#/project/"+project.getKey();
		
		Map<String, Object> data = new LinkedHashMap<String, Object>();
		data.put("header","Project "+project.getName());
		data.put("body",actor.getFirstName()+" created a project");
		data.put("actorPhoto", actor.getIconUrl());
		data.put("actionLabel","Open");
		data.put("actionUrl",actionUrl);
		
		String mailHtml = TemplatingService.getInstance().compile(data, "base.html.vm");
		String mailPlaintext = TemplatingService.getInstance().compile(data, "base.txt.vm");
		
		this.sendMessage("Project "+project.getName(), mailPlaintext, mailHtml, actor, actor);
		
	}
	
}
