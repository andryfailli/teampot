package com.google.teampot.model;

import com.google.api.server.spi.config.AnnotationBoolean;
import com.google.api.server.spi.config.ApiResourceProperty;
import com.google.teampot.transformer.Ref2EntityTransformer;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Load;
import com.googlecode.objectify.annotation.Subclass;

@Subclass(index=true)
public class UserActivityEvent extends ActivityEvent {

	@Load
	private Ref<User> user;

	public UserActivityEvent() {
		// TODO Auto-generated constructor stub
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

}
