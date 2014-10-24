package com.google.teampot.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.api.server.spi.config.AnnotationBoolean;
import com.google.api.server.spi.config.ApiResourceProperty;
import com.google.teampot.transformer.Ref2EntityTransformer;
import com.google.teampot.transformer.Ref2StringTransformer;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Parent;

import de.danielbechler.diff.node.DiffNode;

@Entity
public class ActivityEvent extends BaseEntity {

	@Id
	private Long id;
	
	@Parent
	protected Ref<Project> project;
	
	private Ref<User> actor;
	
	@Index
	private Date timestamp;
	
	//TODO: valutare se rimuovere bidi-ref
	private List<Ref<Comment>> comments;
	
	private Map<String,EntityDiff> diffs; 

	public ActivityEvent() {
		this.comments = new ArrayList<Ref<Comment>>();
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
	
	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	@ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
	public List<Ref<Comment>> getComments() {
		return comments;
	}

	@ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
	public void setComments(List<Ref<Comment>> comments) {
		this.comments = comments;
	}
	
	@ApiResourceProperty(name = "comments")
	public int getCommentsCount() {
		if (this.comments != null)
			return this.comments.size();
		else return 0;
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
	
}
