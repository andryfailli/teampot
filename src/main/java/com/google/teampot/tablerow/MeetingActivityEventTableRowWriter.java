package com.google.teampot.tablerow;

import java.util.ArrayList;
import java.util.List;

import com.google.api.services.bigquery.model.TableFieldSchema;
import com.google.teampot.model.MeetingActivityEvent;
import com.google.teampot.model.Meeting;
import com.googlecode.objectify.Ref;

public class MeetingActivityEventTableRowWriter extends ActivityEventTableRowWriter {

	public MeetingActivityEventTableRowWriter(MeetingActivityEvent activityEvent) {
		super(activityEvent);
		
		row.set("verb",activityEvent.getVerbString());
		
		Ref<Meeting> meetingRef = (Ref<Meeting>) activityEvent.getData();
		if (meetingRef != null) {
			Meeting meeting = meetingRef.get();
			row.set("hasPoll", meeting.hasPoll());
		}
	}
	
	public static List<TableFieldSchema> getTableSchema() {
		List<TableFieldSchema> schema = new ArrayList<TableFieldSchema>();
		
		schema.addAll(ActivityEventTableRowWriter.getTableSchema());
		
		schema.add(new TableFieldSchema().setName("verb").setType("STRING"));
		schema.add(new TableFieldSchema().setName("hasPoll").setType("BOOLEAN"));
				
		return schema;
	}

}
