package com.google.teampot.tablerow;

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
			row.set("dueDate", task.getDueDateTimestamp());
			row.set("completed", task.isCompleted());
			if (task.getAssignee() != null) {
				row.set("assignee", task.getAssigneeEntity().getEmail());
				row.set("assigneeId", task.getAssigneeEntity().getKey());
			}
		}
		
	}

}
