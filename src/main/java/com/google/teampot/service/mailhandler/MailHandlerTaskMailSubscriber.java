package com.google.teampot.service.mailhandler;

import org.apache.commons.mail.util.MimeMessageParser;

import com.google.teampot.model.Project;
import com.google.teampot.model.Task;
import com.google.teampot.model.User;
import com.google.teampot.service.TaskService;

public class MailHandlerTaskMailSubscriber extends MailHandlerSubscriber {

	@Override
	public void processMessage(MimeMessageParser message, Project project, User actor) throws Exception {
		if (message.getSubject().toLowerCase().indexOf("todo")>=0) {
			this.createTask(message, project, actor);
		}
	}
	
	private void createTask(MimeMessageParser message, Project project, User actor) throws Exception {
		
		String taskTitle = message.getSubject()
			.replaceFirst("(?i)todo: ", "")
			.replaceFirst("(?i)todo:", "")
			.replaceFirst("(?i)todo", "")
		;
		
		Task task = new Task();
		task.setProject(project);
		task.setTitle(taskTitle);
		TaskService.getInstance().save(task,actor);
	}

}