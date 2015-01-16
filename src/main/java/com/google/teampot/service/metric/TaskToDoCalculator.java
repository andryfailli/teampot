package com.google.teampot.service.metric;

import java.util.LinkedHashMap;
import java.util.Map;

import com.google.teampot.model.Project;
import com.google.teampot.service.TaskService;
import com.googlecode.objectify.Ref;

public class TaskToDoCalculator extends MetricCalculator {
	
	public static final String TASK_TODO_COUNT = "TASK_TODO_COUNT";
	public static final String TASK_TODO_PERCENT = "TASK_TODO_PERCENT";

	public TaskToDoCalculator() {
		
	}
	
	@Override
	public boolean needsBigQuery() {
		return false;
	}

	@Override
	public Map<String,Object> computeValues(Ref<Project> project) {
		Map<String,Object> metrics = new LinkedHashMap<String, Object>();
		
		int todocount = TaskService.getInstance().countToDoForProject(project);
		int tot = project.get().getTasksCount();
		float perc = tot>0 ? (float)todocount / tot : 0;
			
		metrics.put(TASK_TODO_COUNT,todocount);
		metrics.put(TASK_TODO_PERCENT,perc);
		return metrics;
	}

}
