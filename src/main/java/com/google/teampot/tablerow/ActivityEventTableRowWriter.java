package com.google.teampot.tablerow;

import java.util.ArrayList;
import java.util.List;

import com.google.api.services.bigquery.model.TableFieldSchema;
import com.google.api.services.bigquery.model.TableRow;
import com.google.teampot.model.ActivityEvent;
import com.google.teampot.model.BaseEntity;
import com.googlecode.objectify.Ref;

public abstract class ActivityEventTableRowWriter {

	protected TableRow row;
	
	public ActivityEventTableRowWriter(ActivityEvent activityEvent) {
		this.row = new TableRow();
		
		if (activityEvent.getTimestamp() != null) {
			row.set("timestamp", activityEvent.getTimestampTimestamp()/1000);
		}
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
	
	public static List<TableFieldSchema> getTableSchema() {
		List<TableFieldSchema> schema = new ArrayList<TableFieldSchema>();
		
		schema.add(new TableFieldSchema().setName("timestamp").setType("TIMESTAMP"));
		schema.add(new TableFieldSchema().setName("actor").setType("STRING"));
		schema.add(new TableFieldSchema().setName("actorId").setType("STRING"));
		schema.add(new TableFieldSchema().setName("type").setType("STRING"));
		schema.add(new TableFieldSchema().setName("project").setType("STRING"));
		schema.add(new TableFieldSchema().setName("projectId").setType("STRING"));
		schema.add(new TableFieldSchema().setName("dataId").setType("STRING"));
		
		return schema;
	}

}
