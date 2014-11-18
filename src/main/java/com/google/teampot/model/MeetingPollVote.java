package com.google.teampot.model;

import java.util.Date;

import com.google.api.server.spi.config.AnnotationBoolean;
import com.google.api.server.spi.config.ApiResourceProperty;
import com.googlecode.objectify.Ref;

public class MeetingPollVote {
	
	private Ref<User> user;
	
	private Date date;
	
	private boolean result;
	
	public MeetingPollVote() {
		
	}

	@ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
	public Ref<User> getUser() {
		return user;
	}

	@ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
	public void setUser(Ref<User> user) {
		this.user = user;
	}
	
	@ApiResourceProperty(name = "user")
	public User getUserEntity() {
		return user.get();
	}

	@ApiResourceProperty(name = "user")
	public void setUserEntity(User user) {
		this.user = Ref.create(user);
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public boolean isResult() {
		return result;
	}

	public void setResult(boolean result) {
		this.result = result;
	}
	
}
