package com.google.teampot.model;

import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

public class Task {

	private String title;
	private String description;
	private int priority;
	private User assignee;
	private boolean completed;
	private Date dueDate;
	private Date creationDate;
	private Set<DriveFile> files;

	public Task() {
		this.files = new LinkedHashSet<DriveFile>();
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public User getAssignee() {
		return assignee;
	}

	public void setAssignee(User assignee) {
		this.assignee = assignee;
	}

	public boolean isCompleted() {
		return completed;
	}

	public void setCompleted(boolean completed) {
		this.completed = completed;
	}

	public Date getDueDate() {
		return dueDate;
	}

	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public Set<DriveFile> getFiles() {
		return files;
	}

	public void setFiles(Set<DriveFile> files) {
		this.files = files;
	}

}
