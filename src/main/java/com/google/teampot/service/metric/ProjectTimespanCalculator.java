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

public class ProjectTimespanCalculator extends MetricCalculator {
	
	public static final String PROJECT_TIMESPAN_START = "PROJECT_TIMESPAN_START";
	public static final String PROJECT_TIMESPAN_END = "PROJECT_TIMESPAN_END";

	public ProjectTimespanCalculator() {
		
	}

	@Override
	public Map<String,Object> computeValues(Ref<Project> project) {
		Map<String,Object> metrics = new LinkedHashMap<String, Object>();
		
		Long start = null;
		Long end = null;
		
		String query = "SELECT   MIN(TIMESTAMP_TO_SEC(timestamp)) AS start,  MAX(TIMESTAMP_TO_SEC(timestamp)) AS end FROM   [teampot.MeetingActivityEvent], [teampot.MemberActivityEvent], [teampot.ProjectActivityEvent], [teampot.TaskActivityEvent] WHERE projectId = '"+project.getKey().getString()+"' LIMIT 1";
		
		try {
			List<TableRow> rows = AnalyticsService.getInstance().query(query);
			
			if (rows.size()==1) {
				TableRow row = rows.get(0);
				start = Long.parseLong((String) row.get("start"));
				end = Long.parseLong((String) row.get("end"));
			}
				
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (GeneralSecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
		metrics.put(PROJECT_TIMESPAN_START,start);
		metrics.put(PROJECT_TIMESPAN_END,end);
		return metrics;
	}

}
