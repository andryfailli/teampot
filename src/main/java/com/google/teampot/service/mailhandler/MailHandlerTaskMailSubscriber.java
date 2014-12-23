package com.google.teampot.service.mailhandler;

import java.util.ArrayList;
import java.util.List;

import javax.mail.Address;

import org.apache.commons.mail.util.MimeMessageParser;

import com.google.teampot.model.Project;
import com.google.teampot.model.Task;
import com.google.teampot.model.User;
import com.google.teampot.service.TaskService;
import com.google.teampot.service.UserService;
import com.google.teampot.util.AppHelper;

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
		task.setDescription(message.getPlainContent());
		
		// find assingee
		User assignee = null;
		List<Address> addresses = new ArrayList<Address>();
		addresses.addAll(message.getTo());
		addresses.addAll(message.getCc());
		addresses.addAll(message.getBcc());
		for (Address address : addresses) {
			if (!AppHelper.isAppEmail(address.toString())) {
				User user = UserService.getInstance().getUser(address.toString());
				if (user != null && project.hasUser(user)) {
					task.setAssigneeEntity(user);
					break;
				}
			}
		}
		
		TaskService.getInstance().save(task,actor);
	}

}