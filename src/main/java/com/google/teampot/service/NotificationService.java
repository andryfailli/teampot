package com.google.teampot.service;

import java.io.UnsupportedEncodingException;
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
	
	private Message prepareMessage(String subject, Project recipient, User sender) throws UnsupportedEncodingException, MessagingException {
		Message msg = new MimeMessage(session);
		msg.setFrom(new InternetAddress(Config.get(Config.SERVICE_ACCOUNT_EMAIL), "TeamPot"));
        if (sender != null) {
        	msg.setReplyTo(new Address[]{new InternetAddress(sender.getEmail(), sender.getFirstName()+" "+sender.getLastName())});
        }
        msg.addRecipient(Message.RecipientType.TO, new InternetAddress(recipient.getGroupEmail(), recipient.getName()+" (TeamPot)"));
        msg.setSubject(subject);
        return msg;
	}
	
	public void sendMessage(String subject, String plainBody, User recipient, User sender) throws UnsupportedEncodingException, MessagingException {
		Message msg = this.prepareMessage(subject, recipient, sender);
		msg.setText(plainBody);
        Transport.send(msg);
	}
	
	public void sendMessage(String subject, String plainBody, Project recipient, User sender) throws UnsupportedEncodingException, MessagingException {
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
	
	public void sendMessage(String subject, String plainBody, String htmlBody, Project recipient, User sender) throws UnsupportedEncodingException, MessagingException {
		Message msg = this.prepareMessage(subject, recipient, sender);
		
		msg.setText(plainBody);
		
		Multipart mp = new MimeMultipart();
		
		MimeBodyPart htmlPart = new MimeBodyPart();
		htmlPart.setContent(htmlBody, "text/html");
		mp.addBodyPart(htmlPart);
		
		msg.setContent(mp);
		
        Transport.send(msg);
	}
	
}
