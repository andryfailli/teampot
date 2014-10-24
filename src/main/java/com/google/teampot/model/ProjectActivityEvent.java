package com.google.teampot.model;

import com.google.api.server.spi.config.AnnotationBoolean;
import com.google.api.server.spi.config.ApiResourceProperty;
import com.google.teampot.transformer.Enum2StringTransformer;
import com.google.teampot.transformer.Ref2EntityTransformer;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Load;
import com.googlecode.objectify.annotation.Subclass;

@Subclass(index=true)
public class ProjectActivityEvent extends ActivityEvent {
	
	private EntityActivityEventVerb verb;

	public ProjectActivityEvent() {
		// TODO Auto-generated constructor stub
	}
	
	public ProjectActivityEvent(Project project, User actor, EntityActivityEventVerb verb) {
		super();
		this.setProject(project);
		this.setActor(actor);
		this.setVerb(verb);
	}
	
	public ProjectActivityEvent(Project project, User actor) {
		this(project,actor,null);
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
	public Project getProjectEntity() {
		Ref2EntityTransformer<Project> t = new Ref2EntityTransformer<Project>();
		return t.transformTo(this.project);
	}	

	@ApiResourceProperty(name = "project")
	public void setProjectEntity(Project task) {
		Ref2EntityTransformer<Project> t = new Ref2EntityTransformer<Project>();
		this.project = t.transformFrom(task);
	}

	@ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
	public EntityActivityEventVerb getVerb() {
		return verb;
	}

	@ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
	public void setVerb(EntityActivityEventVerb verb) {
		this.verb = verb;
	}
	
	@ApiResourceProperty(name = "verb")
	public String getVerbString() {
		Enum2StringTransformer<EntityActivityEventVerb> t = new Enum2StringTransformer<EntityActivityEventVerb>(EntityActivityEventVerb.class);
		return t.transformTo(this.verb);
	}	

	@ApiResourceProperty(name = "verb")
	public void setVerbString(String verb) {
		Enum2StringTransformer<EntityActivityEventVerb> t = new Enum2StringTransformer<EntityActivityEventVerb>(EntityActivityEventVerb.class);
		this.verb = t.transformFrom(verb);
	}

}
