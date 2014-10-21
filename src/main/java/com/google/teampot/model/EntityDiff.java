package com.google.teampot.model;

public class EntityDiff {

	private String path;
	
	private Object oldValue;
	
	private Object newValue;
	
	public EntityDiff() {

	}
	
	public EntityDiff(String path) {
		this.path = path;
	}
	
	public EntityDiff(String path, Object oldValue, Object newValue) {
		this(path);
		this.oldValue = oldValue;
		this.newValue = newValue;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public Object getOldValue() {
		return oldValue;
	}

	public void setOldValue(Object oldValue) {
		this.oldValue = oldValue;
	}

	public Object getNewValue() {
		return newValue;
	}

	public void setNewValue(Object newValue) {
		this.newValue = newValue;
	}

}
