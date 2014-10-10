package com.google.teampot.model;

import com.google.api.server.spi.config.AnnotationBoolean;
import com.google.api.server.spi.config.ApiResourceProperty;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

@Entity
public class User extends BaseEntity {

	@Id
	private Long id;
	
	private String email;
	
	private String firstName;
	
	private String lastName;
	
	private String iconUrl;
	
	public User() {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	@ApiResourceProperty(name = "id")
	public String getKey() {
		return Key.create(this.getClass(), this.getId()).getString();
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getIconUrl() {
		return iconUrl;
	}

	public void setIconUrl(String iconUrl) {
		this.iconUrl = iconUrl;
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
