package com.google.teampot.model;

import java.util.Date;

import com.google.api.server.spi.config.AnnotationBoolean;
import com.google.api.server.spi.config.ApiResourceProperty;
import com.google.teampot.transformer.Date2TimestampTransformer;
import com.googlecode.objectify.Ref;

public class MeetingPollVote {
	
	private Ref<User> user;
	
	private Date proposedDate;
	
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
	
	@ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
	public void setUser(User user) {
		this.user = Ref.create(user);
	}
	
	@ApiResourceProperty(name = "user")
	public User getUserEntity() {
		return user.get();
	}

	@ApiResourceProperty(name = "user")
	public void setUserEntity(User user) {
		this.user = Ref.create(user);
	}

	@ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
	public Date getProposedDate() {
		return proposedDate;
	}

	@ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
	public void setProposedDate(Date proposedDate) {
		this.proposedDate = proposedDate;
	}
	
	@ApiResourceProperty(name = "proposedDate")
	public Long getProposedDateTimestamp() {
		Date2TimestampTransformer t = new Date2TimestampTransformer();
		return t.transformTo(proposedDate);
	}

	@ApiResourceProperty(name = "proposedDate")
	public void setProposedDateTimestamp(Long proposedDate) {
		Date2TimestampTransformer t = new Date2TimestampTransformer();
		this.proposedDate = t.transformFrom(proposedDate);
	}

	public boolean isResult() {
		return result;
	}

	public void setResult(boolean result) {
		this.result = result;
	}
	
}
