package com.google.teampot.service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import com.google.api.services.bigquery.Bigquery;
import com.google.api.services.bigquery.model.TableDataInsertAllRequest;
import com.google.api.services.bigquery.model.TableDataInsertAllResponse;
import com.google.api.services.bigquery.model.TableRow;
import com.google.teampot.Config;
import com.google.teampot.GoogleServices;
import com.google.teampot.dao.ActivityEventDAO;
import com.google.teampot.model.ActivityEvent;
import com.google.teampot.model.BaseEntity;
import com.google.teampot.util.AppHelper;
import com.googlecode.objectify.Ref;

public class ActivityEventService {

	private static ActivityEventService instance;
	
	private ActivityEventDAO dao; 
	
	private ActivityEventService() {
		this.dao = new ActivityEventDAO();
	}
	
	public static ActivityEventService getInstance() {
		if (instance == null) instance = new ActivityEventService();
		return instance;
	}
	
	private void saveActivityEvent(ActivityEvent activtyEvent) {
		activtyEvent.setTimestamp(new Date());
		dao.save(activtyEvent);
		
		if (Config.get(Config.FEATURE_ANALYTICS).equals(Config.VALUE_TRUE)) streamActivityEvent(activtyEvent);
		
	}
	
	private void streamActivityEvent(ActivityEvent activtyEvent) {
		try {
			Bigquery bigqueryService = GoogleServices.getBigqueryServiceDomainWide();
			
			TableRow row = activtyEvent.getTableRow();

			TableDataInsertAllRequest.Rows rows = new TableDataInsertAllRequest.Rows();
			rows.setJson(row);
			
			TableDataInsertAllRequest content = new TableDataInsertAllRequest().setRows((List)Arrays.asList(rows));
			TableDataInsertAllResponse response = bigqueryService.tabledata().insertAll(AppHelper.getAppId(), "teampot", activtyEvent.getActivityType(), content).execute();
			
			
		} catch (GeneralSecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public ActivityEvent registerActivityEvent(ActivityEvent activtyEvent) {
		this.saveActivityEvent(activtyEvent);
		return activtyEvent;
	}
}
