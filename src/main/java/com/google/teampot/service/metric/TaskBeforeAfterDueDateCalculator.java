package com.google.teampot.service.metric;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.api.services.bigquery.model.TableCell;
import com.google.api.services.bigquery.model.TableRow;
import com.google.teampot.model.Project;
import com.google.teampot.service.AnalyticsService;
import com.google.teampot.service.TaskService;
import com.googlecode.objectify.Ref;

public class TaskBeforeAfterDueDateCalculator extends MetricCalculator {
	
	public static final String TASK_AFTER_DUEDATE_COUNT = "TASK_AFTER_DUEDATE_COUNT";
	public static final String TASK_BEFORE_DUEDATE_COUNT = "TASK_BEFORE_DUEDATE_COUNT";
	public static final String TASK_BEFORE_DUEDATE_PERCENT = "TASK_BEFORE_DUEDATE_PERCENT";

	public TaskBeforeAfterDueDateCalculator() {
		
	}
	
	@Override
	public boolean needsBigQuery() {
		return true;
	}

	@Override
	public Map<String,Object> computeValues(Ref<Project> project) {
		Map<String,Object> metrics = new LinkedHashMap<String, Object>();
		
		Long afterDueDateCount = null;
		Long beforeDueDateCount = null;
		Long beforeDueDatePerc = null;
		
		String queryAfter = "SELECT 	 COUNT(*) AS value	FROM  [teampot.TaskActivityEvent] AS activity	WHERE  activity.dueDate IS NOT NULL  AND  activity.verb = 'COMPLETE' AND activity.timestamp > activity.dueDate AND projectId = '"+project.getKey().getString()+"' LIMIT 1";
		String queryBefore = "SELECT 	 COUNT(*) AS value	FROM  [teampot.TaskActivityEvent] AS activity	WHERE  activity.dueDate IS NOT NULL  AND  activity.verb = 'COMPLETE' AND activity.timestamp <= activity.dueDate AND projectId = '"+project.getKey().getString()+"' LIMIT 1";
		
		try {
			List<TableRow> rowsAfter = AnalyticsService.getInstance().query(queryAfter);
			if (rowsAfter.size()==1) {
				List<TableCell> valuesAfter =  (List<TableCell>) rowsAfter.get(0).get("f");
				afterDueDateCount = Long.parseLong((String)valuesAfter.get(0).getV());				
			}
			
			List<TableRow> rowsBefore = AnalyticsService.getInstance().query(queryBefore);
			if (rowsBefore.size()==1) {
				List<TableCell> valuesBefore =  (List<TableCell>) rowsBefore.get(0).get("f");
				beforeDueDateCount = Long.parseLong((String)valuesBefore.get(0).getV());				
			}
			
			int donecount = project.get().getTasksCount() - TaskService.getInstance().countToDoForProject(project);
			beforeDueDatePerc = donecount>0 ? beforeDueDateCount / donecount : 0;
				
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (GeneralSecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
		metrics.put(TASK_AFTER_DUEDATE_COUNT,afterDueDateCount);
		metrics.put(TASK_BEFORE_DUEDATE_COUNT,beforeDueDateCount);
		metrics.put(TASK_BEFORE_DUEDATE_PERCENT,beforeDueDatePerc);
		return metrics;
	}

}
