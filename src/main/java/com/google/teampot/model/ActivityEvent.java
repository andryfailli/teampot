package com.google.teampot.model;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import com.google.api.server.spi.config.AnnotationBoolean;
import com.google.api.server.spi.config.ApiResourceProperty;
import com.google.teampot.transformer.Date2TimestampTransformer;
import com.google.teampot.transformer.Ref2EntityTransformer;
import com.google.teampot.transformer.Ref2StringTransformer;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Load;
import com.googlecode.objectify.annotation.Parent;

@Entity
@Cache
public abstract class ActivityEvent extends BaseEntity {

	@Id
	private Long id;
	
	@Parent
	protected Ref<Project> project;
	
	@Load
	protected Ref<? extends BaseEntity> data;
	
	private Ref<User> actor;
	
	@Index
	private Date timestamp;
	
	private Map<String,EntityDiff> diffs; 

	public ActivityEvent() {
		this.timestamp = new Date();
		this.diffs = new LinkedHashMap<String, EntityDiff>();
	}
	
	public ActivityEvent(Ref<Project> project) {
		this();
		this.setProject(project);
	}
	
	public ActivityEvent(Project project) {
		this();
		this.setProject(project);
	}
	
	@Override
	@ApiResourceProperty(name = "id")
	public String getKey() {
		Ref<Project> parent = this.getProject();
		if (parent != null)
			return Key.create(parent.getKey(),this.getClass(), this.getId()).getString();
		else return null;
	}
	
	@ApiResourceProperty(name = "id")
	public void setKey(String key) {
		Key entityKey = Key.create(key);
		Ref parentRef = Ref.create(entityKey.getParent());
		this.setProject(parentRef);
		this.setId(entityKey.getId());
	}
	

	@ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
	public Ref<User> getActor() {
		return actor;
	}

	@ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
	public void setActor(Ref<User> actor) {
		this.actor = actor;
	}
	
	@ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
	public void setActor(User actor) {
		this.actor = Ref.create(actor);
	}
	
	@ApiResourceProperty(name = "actor")
	public User getActorEntity() {
		Ref2EntityTransformer<User> t = new Ref2EntityTransformer<User>();
		return t.transformTo(this.actor);
	}	

	@ApiResourceProperty(name = "actor")
	public void setActorEntity(User actor) {
		Ref2EntityTransformer<User> t = new Ref2EntityTransformer<User>();
		this.actor = t.transformFrom(actor);
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
	
	public String getTimestampGroup() {
		long diff = (new Date()).getTime() - this.timestamp.getTime();
		
		if (diff < 86400000) return "today";
		else if (diff < 172800000L) return "yesterday";
		else if (diff < 604800000L) return "last week";
		else if (diff < 31536000000L) return "last year";
		else return "years ago";
	}

	@ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
	public Ref<Project> getProject() {
		return project;
	}

	@ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
	public void setProject(Ref<Project> project) {
		this.project = project;
	}
	
	@ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
	public void setProject(Project project) {
		this.project = Ref.create(project);
	}
	
	@ApiResourceProperty(name = "project")
	public String getProjectKey() {
		Ref2StringTransformer<Project> t = new Ref2StringTransformer<Project>();
		return t.transformTo(this.project);
	}	

	@ApiResourceProperty(name = "project")
	public void setProjectKey(String project) {
		Ref2StringTransformer<Project> t = new Ref2StringTransformer<Project>();
		this.project = t.transformFrom(project);
	}

	@ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
	public Long getId() {
		return id;
	}

	@ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
	public void setId(Long id) {
		this.id = id;
	}
	
	@ApiResourceProperty(name = "activityType")
	public String getActivityType() {
		return this.getClass().getSimpleName();
	}

	@ApiResourceProperty(name = "diffs")
	public Map<String,EntityDiff> getDiffs() {
		return diffs;
	}

	@ApiResourceProperty(name = "diffs")
	public void setDiffs(Map<String,EntityDiff> diffs) {
		this.diffs = diffs;
	}

	@ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
	public Ref<? extends BaseEntity> getData() {
		return data;
	}

	@ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
	public void setData(Ref<? extends BaseEntity> data) {
		this.data = data;
	}
	
	@ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
	public Map<String,String> getAdditionalData() {
		return null;
	}
	
}
