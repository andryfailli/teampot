package com.google.teampot.model;

import com.google.api.server.spi.config.AnnotationBoolean;
import com.google.api.server.spi.config.ApiResourceProperty;
import com.google.teampot.transformer.Enum2StringTransformer;
import com.google.teampot.transformer.Ref2EntityTransformer;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Load;
import com.googlecode.objectify.annotation.Subclass;

@Subclass(index=true)
public class MemberActivityEvent extends ActivityEvent {

	@Load
	private Ref<User> user;
	
	private MemberActivityEventVerb verb;

	public MemberActivityEvent() {
		// TODO Auto-generated constructor stub
	}
	
	public MemberActivityEvent(Project project, User actor, MemberActivityEventVerb verb) {
		super();
		this.setProject(project);
		this.setActor(actor);
		this.setVerb(verb);
	}
	
	public MemberActivityEvent(Project project, User actor) {
		this(project,actor,null);
	}

	@ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
	public Ref<User> getUser() {
		return user;
	}

	@ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
	public void setUser(Ref<User> user) {
		this.user = user;
	}
	
	@ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
	public void setUser(User user) {
		this.user = Ref.create(user);
	}
	
	@ApiResourceProperty(name = "user")
	public User getUserEntity() {
		Ref2EntityTransformer<User> t = new Ref2EntityTransformer<User>();
		return t.transformTo(this.user);
	}	

	@ApiResourceProperty(name = "user")
	public void setUserEntity(User user) {
		Ref2EntityTransformer<User> t = new Ref2EntityTransformer<User>();
		this.user = t.transformFrom(user);
	}

	@ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
	public MemberActivityEventVerb getVerb() {
		return verb;
	}

	@ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
	public void setVerb(MemberActivityEventVerb verb) {
		this.verb = verb;
	}
	
	@ApiResourceProperty(name = "verb")
	public String getVerbString() {
		Enum2StringTransformer<MemberActivityEventVerb> t = new Enum2StringTransformer<MemberActivityEventVerb>(MemberActivityEventVerb.class);
		return t.transformTo(this.verb);
	}	

	@ApiResourceProperty(name = "verb")
	public void setVerbString(String verb) {
		Enum2StringTransformer<MemberActivityEventVerb> t = new Enum2StringTransformer<MemberActivityEventVerb>(MemberActivityEventVerb.class);
		this.verb = t.transformFrom(verb);
	}

}
