package com.google.teampot.tablerow;

import com.google.teampot.model.ProjectActivityEvent;

public class ProjectActivityEventTableRowWriter extends ActivityEventTableRowWriter {

	public ProjectActivityEventTableRowWriter(ProjectActivityEvent activityEvent) {
		super(activityEvent);
		
		row.set("verb",activityEvent.getVerbString());
	}

}
