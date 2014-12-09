package com.google.teampot.model;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.server.spi.config.AnnotationBoolean;
import com.google.api.server.spi.config.ApiResourceProperty;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

@Entity
@Cache
public class User extends BaseEntity {

	@Id
	private Long id;
	
	@Index
	private String email;
	
	private String firstName;
	
	@Index
	private String firstNameLowerCase;

	private String lastName;
	
	@Index
	private String lastNameLowerCase;
	
	private String iconUrl;
	
	private boolean enabled;

	private String accessToken;
	
	private String refreshToken;
	
	public User() {
		this.enabled = false;
	}
	
	public User(String email) {
		this.enabled = false;
		this.setEmail(email);
	}
	
	@Override
	@ApiResourceProperty(name = "id")
	public String getKey() {
		return Key.create(this.getClass(), this.getId()).getString();
	}
	
	@ApiResourceProperty(name = "id")
	public void setKey(String key) {
		Key entityKey = Key.create(key);
		this.setId(entityKey.getId());
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email != null ? email.toLowerCase() : null;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstNameLowerCase = firstName != null ? firstName.toLowerCase() : null;
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastNameLowerCase = lastName != null ? lastName.toLowerCase() : null;
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
	
	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	@ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
	public String getAccessToken() {
		return accessToken;
	}

	@ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	@ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
	public String getRefreshToken() {
		return refreshToken;
	}

	@ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}
	
	@ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
	public void setTokens(GoogleCredential credential) {
		this.accessToken = credential.getAccessToken();
		this.refreshToken = credential.getRefreshToken();
	}

}
