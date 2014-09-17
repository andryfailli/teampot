package com.google.teampot.model;

public class UserActivityEvent extends ActivityEvent {

	private User user;

	public UserActivityEvent() {
		// TODO Auto-generated constructor stub
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

}
