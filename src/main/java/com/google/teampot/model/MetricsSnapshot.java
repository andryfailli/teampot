package com.google.teampot.model;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import com.google.api.server.spi.config.AnnotationBoolean;
import com.google.api.server.spi.config.ApiResourceProperty;
import com.google.teampot.transformer.Date2TimestampTransformer;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

@Entity
@Cache
public class MetricsSnapshot extends BaseEntity {

	@Id
	private Long id;
	
	@Index
	private Date timestamp;
	
	private Map<String,Float> metrics; 

	public MetricsSnapshot() {
		this.timestamp = new Date();
		this.metrics = new LinkedHashMap<String, Float>();
	}
	
	@Override
	@ApiResourceProperty(name = "id")
	public String getKey() {
		return Key.create(this.getClass(), this.getId()).getString();
	}
	
	@ApiResourceProperty(name = "id")
	public void setKey(String key) {
		Key entityKey = Key.create(key);
		this.setId(entityKey.getId());
	}
	
	@ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
	public Date getTimestamp() {
		return timestamp;
	}

	@ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}
	
	@ApiResourceProperty(name = "timestamp")
	public Long getTimestampTimestamp() {
		Date2TimestampTransformer t = new Date2TimestampTransformer();
		return t.transformTo(timestamp);
	}

	@ApiResourceProperty(name = "timestamp")
	public void setTimestampTimestamp(Long timestamp) {
		Date2TimestampTransformer t = new Date2TimestampTransformer();
		this.timestamp = t.transformFrom(timestamp);
	}

	@ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
	public Long getId() {
		return id;
	}

	@ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
	public void setId(Long id) {
		this.id = id;
	}
	
	public Map<String,Float> getMetrics() {
		return metrics;
	}
	
	public void addMetric(String name, Float value) {
		metrics.put(name, value);
	}

	public void setMetrics(Map<String,Float> metrics) {
		this.metrics = metrics;
	}
	
}
