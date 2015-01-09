package com.google.teampot.model;

import com.google.api.server.spi.config.AnnotationBoolean;
import com.google.api.server.spi.config.ApiResourceProperty;
import com.google.teampot.transformer.Enum2StringTransformer;
import com.google.teampot.transformer.Ref2EntityTransformer;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Subclass;

@Subclass(index=true)
public class MeetingActivityEvent extends ActivityEvent {
	
	private MeetingActivityEventVerb verb;

	public MeetingActivityEvent() {
		// TODO Auto-generated constructor stub
	}
	
	public MeetingActivityEvent(Meeting meeting, User actor, MeetingActivityEventVerb verb) {
		super();
		this.setMeeting(meeting);
		this.setActor(actor);
		this.setVerb(verb);
	}

	@ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
	public Ref<Meeting> getMeeting() {
		return (Ref<Meeting>) this.data;
	}

	@ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
	public void setMeeting(Ref<Meeting> meeting) {
		this.data = meeting;
		this.setProject(meeting.get().getProject());
	}
	
	@ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
	public void setMeeting(Meeting meeting) {
		this.data = Ref.create(meeting);
		this.setProject(meeting.getProject());
	}
	
	@ApiResourceProperty(name = "meeting")
	public Meeting getMeetingEntity() {
		Ref2EntityTransformer<Meeting> t = new Ref2EntityTransformer<Meeting>();
		return t.transformTo((Ref<Meeting>) this.data);
	}	

	@ApiResourceProperty(name = "meeting")
	public void setMeetingEntity(Meeting meeting) {
		Ref2EntityTransformer<Meeting> t = new Ref2EntityTransformer<Meeting>();
		this.data = t.transformFrom(meeting);
	}

	@ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
	public MeetingActivityEventVerb getVerb() {
		return verb;
	}

	@ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
	public void setVerb(MeetingActivityEventVerb verb) {
		this.verb = verb;
	}
	
	@ApiResourceProperty(name = "verb")
	@Override
	public String getVerbString() {
		Enum2StringTransformer<MeetingActivityEventVerb> t = new Enum2StringTransformer<MeetingActivityEventVerb>(MeetingActivityEventVerb.class);
		return t.transformTo(this.verb);
	}	

	@ApiResourceProperty(name = "verb")
	public void setVerbString(String verb) {
		Enum2StringTransformer<MeetingActivityEventVerb> t = new Enum2StringTransformer<MeetingActivityEventVerb>(MeetingActivityEventVerb.class);
		this.verb = t.transformFrom(verb);
	}

}
