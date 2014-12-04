package com.google.teampot.dao;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.google.teampot.model.User;

import static com.google.teampot.OfyService.ofy;

public class UserDAO extends BaseEntityDAO<User>{
	
	public UserDAO() {
		super(User.class);
	}
	
	public User getByEmail(String email) {
		List<User> list = ofy().load().type(User.class).filter("email", email).limit(1).list();
		if (list.size()==1)
			return list.get(0);
		else
			return null;
	}

	public List<User> search(String q) {
		q = q.toLowerCase();
		
		HashSet set = new HashSet();
				
		set.addAll(ofy().load().type(User.class).filter("firstNameLowerCase >=", q).filter("firstNameLowerCase <=", q+"\ufffd").list());
		set.addAll(ofy().load().type(User.class).filter("lastNameLowerCase >=", q).filter("lastNameLowerCase <=", q+"\ufffd").list());
		
		return new ArrayList<User>(set);
	}

}
