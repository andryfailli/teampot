package com.google.teampot.model;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class Project {

	private List<ActivityEvent> activityEvents;
	private List<Meeting> meetings;
	private List<Task> tasks;
	private Set<User> users;
	private Set<DriveFile> files;
	private String name;

	public Project() {
		this.activityEvents = new ArrayList<ActivityEvent>();
		this.meetings = new ArrayList<Meeting>();
		this.tasks = new ArrayList<Task>();
		this.users = new LinkedHashSet<User>();
		this.files = new LinkedHashSet<DriveFile>();
	}
	
	public Project(String name) {
		this();
		this.name = name;
	}

	public List<ActivityEvent> getActivityEvents() {
		return activityEvents;
	}

	public void setActivityEvents(List<ActivityEvent> activityEvents) {
		this.activityEvents = activityEvents;
	}

	public List<Meeting> getMeetings() {
		return meetings;
	}

	public void setMeetings(List<Meeting> meetings) {
		this.meetings = meetings;
	}

	public List<Task> getTasks() {
		return tasks;
	}

	public void setTasks(List<Task> tasks) {
		this.tasks = tasks;
	}

	public Set<User> getUsers() {
		return users;
	}

	public void setUsers(Set<User> users) {
		this.users = users;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Set<DriveFile> getFiles() {
		return files;
	}

	public void setFiles(Set<DriveFile> files) {
		this.files = files;
	}

}
