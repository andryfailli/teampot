package com.google.teampot.tablerow;

import com.google.teampot.model.MeetingActivityEvent;

public class MeetingActivityEventTableRowWriter extends ActivityEventTableRowWriter {

	public MeetingActivityEventTableRowWriter(MeetingActivityEvent activityEvent) {
		super(activityEvent);
		
		row.set("verb",activityEvent.getVerbString());
	}

}
