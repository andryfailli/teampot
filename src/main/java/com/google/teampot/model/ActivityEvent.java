package com.google.teampot.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.api.server.spi.config.AnnotationBoolean;
import com.google.api.server.spi.config.ApiResourceProperty;
import com.google.teampot.transformer.Ref2StringTransformer;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Parent;

@Entity
public class ActivityEvent extends BaseEntity {

	@Id
	private Long id;
	
	@Parent
	private Ref<Project> project;
	
	private Ref<User> actor;
	
	private Date timestamp;
	
	//TODO: valutare se rimuovere bidi-ref
	private List<Ref<Comment>> comments;

	public ActivityEvent() {
		this.comments = new ArrayList<Ref<Comment>>();
		this.timestamp = new Date();
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
		return Key.create(this.getProject().getKey(),this.getClass(), this.getId()).getString();
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
	public String getActorKey() {
		Ref2StringTransformer<User> t = new Ref2StringTransformer<User>();
		return t.transformTo(this.actor);
	}	

	@ApiResourceProperty(name = "actor")
	public void setActorKey(String actor) {
		Ref2StringTransformer<User> t = new Ref2StringTransformer<User>();
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
	
}
