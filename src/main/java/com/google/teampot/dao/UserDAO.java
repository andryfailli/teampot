package com.google.teampot.dao;

import com.google.teampot.model.User;

public class UserDAO extends BaseEntityDAO<User>{
	
	public UserDAO() {
		super(User.class);
	}

}
