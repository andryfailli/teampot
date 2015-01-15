package com.google.teampot.service.metric;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.api.services.bigquery.model.TableRow;
import com.google.teampot.model.Project;
import com.google.teampot.service.AnalyticsService;
import com.googlecode.objectify.Ref;

public class TaskAvgCompleteTimeCalculator extends MetricCalculator {
	
	public static final String TASK_AVG_COMPLETE_TIME = "TASK_AVG_COMPLETE_TIME";

	public TaskAvgCompleteTimeCalculator() {
		
	}

	@Override
	public Map<String,Object> computeValues(Ref<Project> project) {
		Map<String,Object> metrics = new LinkedHashMap<String, Object>();
		
		Double value = null;
		
		String query = "SELECT AVG(TIMESTAMP_TO_SEC(activity2.timestamp)-TIMESTAMP_TO_SEC(activity1.timestamp)) AS value FROM   [teampot.TaskActivityEvent] AS activity1  JOIN  [teampot.TaskActivityEvent] AS activity2 ON activity1.dataId=activity2.dataId WHERE activity1.verb = 'CREATE' AND activity2.verb = 'COMPLETE' AND activity1.projectId = '"+project.getKey().getString()+"' AND activity2.projectId = '"+project.getKey().getString()+"' LIMIT 1";
		
		try {
			List<TableRow> rows = AnalyticsService.getInstance().query(query);
			
			if (rows.size()==1) {
				TableRow row = rows.get(0);
				value = Double.parseDouble((String) row.get("value"));
			}
				
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (GeneralSecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		metrics.put(TASK_AVG_COMPLETE_TIME,value);
		return metrics;
	}

}
