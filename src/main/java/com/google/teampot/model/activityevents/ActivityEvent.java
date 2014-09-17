package com.google.teampot.model.activityevents;

import java.util.Date;
import java.util.List;

import com.google.teampot.model.Comment;
import com.google.teampot.model.User;

public class ActivityEvent {

	private User actor;
	private Date timestamp;
	private List<Comment> comments;

	public ActivityEvent() {
		// TODO Auto-generated constructor stub
	}

	public User getActor() {
		return actor;
	}

	public void setActor(User actor) {
		this.actor = actor;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public List<Comment> getComments() {
		return comments;
	}

	public void setComments(List<Comment> comments) {
		this.comments = comments;
	}

}
