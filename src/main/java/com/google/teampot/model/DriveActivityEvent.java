package com.google.teampot.model;

import com.googlecode.objectify.annotation.Subclass;

@Subclass(index=true)
public class DriveActivityEvent extends ActivityEvent {

	private String file;
	
	public DriveActivityEvent() {
		// TODO Auto-generated constructor stub
	}

	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}

}
