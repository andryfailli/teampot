package com.google.teampot.service.metric;

import java.util.Map;

import com.google.teampot.model.Project;
import com.googlecode.objectify.Ref;

public abstract class MetricCalculator {
	
	public abstract Map<String,Object> computeValues(Ref<Project> project);
	
	public abstract boolean needsBigQuery();

}
