package com.google.teampot.service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.bigquery.Bigquery;
import com.google.api.services.bigquery.model.Dataset;
import com.google.api.services.bigquery.model.DatasetReference;
import com.google.api.services.bigquery.model.GetQueryResultsResponse;
import com.google.api.services.bigquery.model.QueryRequest;
import com.google.api.services.bigquery.model.QueryResponse;
import com.google.api.services.bigquery.model.Table;
import com.google.api.services.bigquery.model.TableFieldSchema;
import com.google.api.services.bigquery.model.TableReference;
import com.google.api.services.bigquery.model.TableRow;
import com.google.api.services.bigquery.model.TableSchema;
import com.google.teampot.Config;
import com.google.teampot.GoogleServices;
import com.google.teampot.model.MetricsSnapshot;
import com.google.teampot.model.Project;
import com.google.teampot.service.metric.ProjectTimespanCalculator;
import com.google.teampot.service.metric.MetricCalculator;
import com.google.teampot.service.metric.TaskAvgCompleteTimeCalculator;
import com.google.teampot.service.metric.TaskBeforeAfterDueDateCalculator;
import com.google.teampot.service.metric.TaskToDoCalculator;
import com.google.teampot.service.metric.UserMostActiveCalculator;
import com.google.teampot.util.AppHelper;
import com.googlecode.objectify.Ref;

public class AnalyticsService {

	private static AnalyticsService instance;
	
	private Set<MetricCalculator> metricCalculators;

	private AnalyticsService() {
		this.metricCalculators = new LinkedHashSet<MetricCalculator>();
		metricCalculators.add(new ProjectTimespanCalculator());
		metricCalculators.add(new TaskToDoCalculator());
		metricCalculators.add(new TaskBeforeAfterDueDateCalculator());
		metricCalculators.add(new TaskAvgCompleteTimeCalculator());
		metricCalculators.add(new UserMostActiveCalculator());
	}

	public static AnalyticsService getInstance() {
		if (instance == null)
			instance = new AnalyticsService();
		return instance;
	}
	
	public void updateAnalytics() {
		//NOOP
		/*
		MetricsSnapshot snapshot = this.getMetrics();
		MetricsSnapshotDAO dao = new MetricsSnapshotDAO();
		dao.save(snapshot);
		*/
	}
	
	public MetricsSnapshot getMetrics(Ref<Project> project) {
		MetricsSnapshot snapshot = new MetricsSnapshot();
		for (MetricCalculator metricCalculator : metricCalculators) {
			if (Config.get(Config.FEATURE_ANALYTICS).equals(Config.VALUE_TRUE) || !metricCalculator.needsBigQuery())
				snapshot.getMetrics().putAll(metricCalculator.computeValues(project));
		}
		return snapshot;
	}

	public List<TableRow> query(String query)	throws IOException, GeneralSecurityException {
		Bigquery bigquery = GoogleServices.getBigqueryServiceDomainWide();
		String projectId = AppHelper.getAppId();

		List<TableRow> rows = null;

		QueryRequest queryRequest = new QueryRequest().setQuery(query);
		QueryResponse queryResponse = bigquery.jobs().query(projectId, queryRequest).execute();
		if (queryResponse.getJobComplete()) {
			rows = queryResponse.getRows();
			if (null == queryResponse.getPageToken()) {
				return rows;
			}
		}

		// This loop polls until results are present, then loops over result pages.
		String pageToken = null;
		while (true) {
			GetQueryResultsResponse queryResults = bigquery
					.jobs()
					.getQueryResults(projectId,	queryResponse.getJobReference().getJobId())
					.setPageToken(pageToken).execute();
			if (queryResults.getJobComplete()) {
				rows.addAll(queryResults.getRows());
				pageToken = queryResults.getPageToken();
				if (null == pageToken) {
					return rows;
				}
			}
		}
	}
	
	
	public void createDataset(String datasetName) throws GeneralSecurityException, IOException {
		Bigquery bigquery = GoogleServices.getBigqueryServiceDomainWide();
		
		Dataset dataset = new Dataset();
	    DatasetReference datasetRef = new DatasetReference();
	    datasetRef.setProjectId(AppHelper.getAppId());
	    datasetRef.setDatasetId(datasetName);
	    dataset.setDatasetReference(datasetRef);
	    
	    try {
	    	bigquery.datasets().insert(AppHelper.getAppId(), dataset).execute();
	    } catch (GoogleJsonResponseException ex) {
			if (ex.getStatusCode() != 409) throw ex;
		}
	}
	
	public void createTable(String datasetName, String tableName, List<TableFieldSchema> schemaFields) throws GeneralSecurityException, IOException {
		Bigquery bigquery = GoogleServices.getBigqueryServiceDomainWide();
		
		TableSchema schema = new TableSchema();
		schema.setFields(schemaFields);
		
		Table table = new Table();
		table.setSchema(schema);
		TableReference tableRef = new TableReference();
		tableRef.setDatasetId(datasetName);
		tableRef.setProjectId(AppHelper.getAppId());
		tableRef.setTableId(tableName);
		table.setTableReference(tableRef);
		
		try {
			bigquery.tables().insert(AppHelper.getAppId(), datasetName, table).execute();
		} catch (GoogleJsonResponseException ex) {
			if (ex.getStatusCode() != 409) throw ex;
		}
	}


}
