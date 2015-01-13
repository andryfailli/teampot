package com.google.teampot.tablerow;

import java.util.ArrayList;
import java.util.List;

import com.google.api.services.bigquery.model.TableFieldSchema;
import com.google.teampot.model.Task;
import com.google.teampot.model.TaskActivityEvent;
import com.googlecode.objectify.Ref;

public class TaskActivityEventTableRowWriter extends ActivityEventTableRowWriter {

	public TaskActivityEventTableRowWriter(TaskActivityEvent activityEvent) {
		super(activityEvent);
		
		row.set("verb",activityEvent.getVerbString());
		
		Ref<Task> taskRef = (Ref<Task>) activityEvent.getData();
		if (taskRef != null) {
			Task task = taskRef.get();
			if (task.getDueDate() != null) {
				row.set("dueDate", task.getDueDateTimestamp()/1000);
			}
			row.set("completed", task.isCompleted());
			if (task.getAssignee() != null) {
				row.set("assignee", task.getAssigneeEntity().getEmail());
				row.set("assigneeId", task.getAssigneeEntity().getKey());
			}
		}
		
	}
	
	public static List<TableFieldSchema> getTableSchema() {
		List<TableFieldSchema> schema = new ArrayList<TableFieldSchema>();
		
		schema.addAll(ActivityEventTableRowWriter.getTableSchema());
		
		schema.add(new TableFieldSchema().setName("verb").setType("STRING"));
		schema.add(new TableFieldSchema().setName("dueDate").setType("TIMESTAMP"));
		schema.add(new TableFieldSchema().setName("completed").setType("BOOLEAN"));
		schema.add(new TableFieldSchema().setName("assignee").setType("STRING"));
		schema.add(new TableFieldSchema().setName("assigneeId").setType("STRING"));
				
		return schema;
	}

}
