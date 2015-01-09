package com.google.teampot.service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

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
			
			TableRow row = new TableRow();
			row.set("timestamp", activtyEvent.getTimestampTimestamp());
			row.set("actor", activtyEvent.getActor().get().getEmail());
			row.set("actorId", activtyEvent.getActor().get().getKey());
			row.set("type",activtyEvent.getActivityType());
			row.set("project",activtyEvent.getProject().get().getMachineName());
			row.set("projectId",activtyEvent.getProject().get().getKey());
			
			Ref<? extends BaseEntity> data = activtyEvent.getData();
			if (data != null) {
				row.set("dataId", data.getKey().getString());
			}

			Map<String,String> additionalData = activtyEvent.getAdditionalData();
			if (additionalData != null) {
				for (Map.Entry<String,String> entry : activtyEvent.getAdditionalData().entrySet()) {
				    row.set("additionalData_"+entry.getKey(), entry.getValue());
				}
			}
						
			TableDataInsertAllRequest.Rows rows = new TableDataInsertAllRequest.Rows();
			rows.setJson(row);
			
			TableDataInsertAllRequest content = new TableDataInsertAllRequest().setRows((List)Arrays.asList(rows));
			TableDataInsertAllResponse response = bigqueryService.tabledata().insertAll("teampot", AppHelper.getAppId(), "activity", content).execute();
			
			
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
