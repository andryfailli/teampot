package com.google.teampot.model;

public class DriveActivityEvent extends ActivityEvent {

	private DriveFile file;
	
	public DriveActivityEvent() {
		// TODO Auto-generated constructor stub
	}

	public DriveFile getFile() {
		return file;
	}

	public void setFile(DriveFile file) {
		this.file = file;
	}

}
