package com.google.teampot.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class Meeting {
	
	private String title;
	private String description;
	private Date timestamp;
	private List<String> agenda;
	private Set<DriveFile> files;
	

	public Meeting() {
		this.agenda = new ArrayList<String>();
		this.files = new LinkedHashSet<DriveFile>();
	}


	public String getTitle() {
		return title;
	}


	public void setTitle(String title) {
		this.title = title;
	}


	public String getDescription() {
		return description;
	}


	public void setDescription(String description) {
		this.description = description;
	}


	public Date getTimestamp() {
		return timestamp;
	}


	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}


	public List<String> getAgenda() {
		return agenda;
	}


	public void setAgenda(List<String> agenda) {
		this.agenda = agenda;
	}


	public Set<DriveFile> getFiles() {
		return files;
	}


	public void setFiles(Set<DriveFile> files) {
		this.files = files;
	}

}
