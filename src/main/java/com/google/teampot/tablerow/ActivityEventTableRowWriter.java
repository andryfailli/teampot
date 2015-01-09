package com.google.teampot.tablerow;

import com.google.api.services.bigquery.model.TableRow;
import com.google.teampot.model.ActivityEvent;
import com.google.teampot.model.BaseEntity;
import com.googlecode.objectify.Ref;

public abstract class ActivityEventTableRowWriter {

	protected TableRow row;
	
	public ActivityEventTableRowWriter(ActivityEvent activityEvent) {
		this.row = new TableRow();
		
		row.set("timestamp", activityEvent.getTimestampTimestamp());
		row.set("actor", activityEvent.getActor().get().getEmail());
		row.set("actorId", activityEvent.getActor().get().getKey());
		row.set("type",activityEvent.getActivityType());
		row.set("project",activityEvent.getProject().get().getMachineName());
		row.set("projectId",activityEvent.getProject().get().getKey());
		
		Ref<? extends BaseEntity> data = activityEvent.getData();
		if (data != null) {
			row.set("dataId", data.getKey().getString());
		}
		
	}
	
	public TableRow getRow() {
		return this.row;
	}

}
