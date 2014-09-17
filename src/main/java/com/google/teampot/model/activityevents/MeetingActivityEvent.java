package com.google.teampot.model.activityevents;

import com.google.teampot.model.Meeting;

public class MeetingActivityEvent extends ActivityEvent {

	private Meeting meeting;

	public MeetingActivityEvent() {
		// TODO Auto-generated constructor stub
	}

	public Meeting getMeeting() {
		return this.meeting;
	}

	public void setMeeting(Meeting meeting) {
		this.meeting = meeting;
	}

}
