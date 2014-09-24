package com.google.teampot.model;

import com.google.api.server.spi.config.AnnotationBoolean;
import com.google.api.server.spi.config.ApiResourceProperty;
import com.google.teampot.transformer.Ref2EntityTransformer;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Load;
import com.googlecode.objectify.annotation.Subclass;

@Subclass(index=true)
public class MeetingActivityEvent extends ActivityEvent {

	@Load
	private Ref<Meeting> meeting;

	public MeetingActivityEvent() {
		// TODO Auto-generated constructor stub
	}

	@ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
	public Ref<Meeting> getMeeting() {
		return this.meeting;
	}

	@ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
	public void setMeeting(Ref<Meeting> meeting) {
		this.meeting = meeting;
	}
	
	@ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
	public void setMeeting(Meeting meeting) {
		this.meeting = Ref.create(meeting);
	}
	
	@ApiResourceProperty(name = "meeting")
	public Meeting getMeetingEntity() {
		Ref2EntityTransformer<Meeting> t = new Ref2EntityTransformer<Meeting>();
		return t.transformTo(this.meeting);
	}	

	@ApiResourceProperty(name = "meeting")
	public void setMeetingEntity(Meeting meeting) {
		Ref2EntityTransformer<Meeting> t = new Ref2EntityTransformer<Meeting>();
		this.meeting = t.transformFrom(meeting);
	}

}
