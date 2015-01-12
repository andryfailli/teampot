package com.google.teampot.tablerow;

import java.util.ArrayList;
import java.util.List;

import com.google.api.services.bigquery.model.TableFieldSchema;
import com.google.teampot.model.ProjectActivityEvent;

public class ProjectActivityEventTableRowWriter extends ActivityEventTableRowWriter {

	public ProjectActivityEventTableRowWriter(ProjectActivityEvent activityEvent) {
		super(activityEvent);
		
		row.set("verb",activityEvent.getVerbString());
	}
	
	public static List<TableFieldSchema> getTableSchema() {
		List<TableFieldSchema> schema = new ArrayList<TableFieldSchema>();
		
		schema.addAll(ActivityEventTableRowWriter.getTableSchema());
		
		schema.add(new TableFieldSchema().setName("verb").setType("STRING"));
				
		return schema;
	}

}
