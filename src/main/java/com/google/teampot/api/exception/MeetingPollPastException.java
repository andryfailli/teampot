package com.google.teampot.api.exception;

import com.google.api.server.spi.response.BadRequestException;

public class MeetingPollPastException extends BadRequestException {

	private static final long serialVersionUID = 1L;

	public MeetingPollPastException(String key) {
		super("Meeting with key "+key+" is a past meeting. You cannot vote.");
	}

}
