package com.google.teampot.dao;

import static com.google.teampot.OfyService.ofy;

import java.util.List;

import com.google.teampot.model.ActivityEvent;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Ref;

public class ActivityEventDAO extends BaseEntityDAO<ActivityEvent>{
	
	public ActivityEventDAO() {
		super(ActivityEvent.class);
	}
	
	@Override
	public List<ActivityEvent> list() {
		return ofy().load().type(ActivityEvent.class).order("-timestamp").list();
	}
	
	@Override
	public List<ActivityEvent> list(String parent) {
		return ofy().load().type(ActivityEvent.class).ancestor(Ref.create(Key.create(parent))).order("-timestamp").list();
	}
	
	public List<ActivityEvent> list(String parent, int page) {
		return ofy().load().type(ActivityEvent.class).ancestor(Ref.create(Key.create(parent))).order("-timestamp").offset(page*this.pageSize).limit(this.pageSize).list();
	}

}
