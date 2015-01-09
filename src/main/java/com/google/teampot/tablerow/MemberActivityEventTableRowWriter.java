package com.google.teampot.tablerow;

import com.google.teampot.model.MemberActivityEvent;

public class MemberActivityEventTableRowWriter extends ActivityEventTableRowWriter {

	public MemberActivityEventTableRowWriter(MemberActivityEvent activityEvent) {
		super(activityEvent);
		
		row.set("verb",activityEvent.getVerbString());
	}

}
