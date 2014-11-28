package com.google.teampot.service;

import java.io.StringWriter;
import java.util.Map;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

public class TemplatingService {

	private static TemplatingService instance;
	
	private TemplatingService() {
		Velocity.init();
	}
	
	public static TemplatingService getInstance() {
		if (instance == null) instance = new TemplatingService();
		return instance;
	}
	
	public String compile(Map<String,Object> data, String vmFileName) {
		
		VelocityContext context = new VelocityContext();
		for (Map.Entry<String, Object> entry : data.entrySet()) {
		    context.put(entry.getKey(), entry.getValue());
		}
		
		Template template = Velocity.getTemplate("WEB-INF/templates/"+vmFileName);
		
		StringWriter sw = new StringWriter();
		template.merge(context, sw);
		
		return sw.getBuffer().toString();
		
	}
	
}
