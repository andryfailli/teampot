package com.google.teampot.dao;

import static com.google.teampot.OfyService.ofy;

import java.util.List;

import com.google.teampot.model.Meeting;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Ref;

public class MeetingDAO extends BaseEntityDAO<Meeting>{
	
	public MeetingDAO() {
		super(Meeting.class);
	}
	
	@Override
	public List<Meeting> list() {
		return ofy().load().type(Meeting.class).order("-timestamp").list();
	}
	
	@Override
	public List<Meeting> list(String parent) {
		return ofy().load().type(Meeting.class).ancestor(Ref.create(Key.create(parent))).order("-timestamp").list();
	}

}
