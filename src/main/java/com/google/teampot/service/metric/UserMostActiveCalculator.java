package com.google.teampot.service.metric;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.api.services.bigquery.model.TableRow;
import com.google.teampot.model.Project;
import com.google.teampot.model.User;
import com.google.teampot.service.AnalyticsService;
import com.google.teampot.service.UserService;
import com.googlecode.objectify.Ref;

public class UserMostActiveCalculator extends MetricCalculator {
	
	public static final String USER_MOST_ACTIVE_LIST = "USER_MOST_ACTIVE_LIST";

	public UserMostActiveCalculator() {
		
	}
	
	@Override
	public boolean needsBigQuery() {
		return true;
	}

	@Override
	public Map<String,Object> computeValues(Ref<Project> project) {
		Map<String,Object> metrics = new LinkedHashMap<String, Object>();
		
		List<User> users = null;
		
		String query = "SELECT  actorId,count(actorId) as n FROM   [teampot.MeetingActivityEvent], [teampot.MemberActivityEvent], [teampot.ProjectActivityEvent], [teampot.TaskActivityEvent] GROUP BY actorId ORDER BY n DESC; AND projectId = '"+project.getKey().getString()+"' LIMIT 5";
		
		try {
			List<TableRow> rows = AnalyticsService.getInstance().query(query);
						
			for (TableRow row : rows) {
				if (users==null) users = new ArrayList<User>();
				users.add(UserService.getInstance().get((String) row.get("actorId")));
			}
				
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (GeneralSecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		metrics.put(USER_MOST_ACTIVE_LIST,users);
		return metrics;
	}

}