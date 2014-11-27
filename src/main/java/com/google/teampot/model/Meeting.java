package com.google.teampot.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.google.api.server.spi.config.AnnotationBoolean;
import com.google.api.server.spi.config.ApiResourceProperty;
import com.google.appengine.api.datastore.Text;
import com.google.teampot.transformer.Ref2StringTransformer;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Parent;

@Entity
public class Meeting extends BaseEntity {
	
	@Id
	private Long id;
	
	@Parent
	private Ref<Project> project;
	
	private String title;
	
	private Text description;
	
	@Index
	private Date timestamp;
	
	private String calendarEventId;
	
	private List<String> agenda;
	
	private Set<String> files;
	
	private Ref<User> organizer;
	
	private MeetingPoll poll;
	
	private String hangoutLink;
	
	public Meeting() {
		this.agenda = new ArrayList<String>();
		this.files = new LinkedHashSet<String>();
	}
	
	@Override
	@ApiResourceProperty(name = "id")
	public String getKey() {
		Ref<Project> parent = this.getProject();
		if (parent != null)
			return Key.create(parent.getKey(),this.getClass(), this.getId()).getString();
		else return null;
	}
	
	@ApiResourceProperty(name = "id")
	public void setKey(String key) {
		Key entityKey = Key.create(key);
		Ref parentRef = Ref.create(entityKey.getParent());
		this.setProject(parentRef);
		this.setId(entityKey.getId());
	}
	
	

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description != null ? description.getValue() : null;
	}

	public void setDescription(String description) {
		this.description = description != null ? new Text(description) : null;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}
	
	public boolean isPast() {
		if (timestamp != null)
			return (new Date()).after(timestamp);
		else
			return false;
	}

	public List<String> getAgenda() {
		return agenda;
	}

	public void setAgenda(List<String> agenda) {
		this.agenda = agenda;
	}

	public Set<String> getFiles() {
		return files;
	}
	
	public void setFiles(Set<String> files) {
		this.files = files;
	}

	@ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
	public Long getId() {
		return id;
	}

	@ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
	public void setId(Long id) {
		this.id = id;
	}

	@ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
	public Ref<Project> getProject() {
		return project;
	}

	@ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
	public void setProject(Ref<Project> project) {
		this.project = project;
	}
	
	@ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
	public void setProject(Project project) {
		this.project = Ref.create(project);
	}
	
	@ApiResourceProperty(name = "project")
	public String getProjectKey() {
		Ref2StringTransformer<Project> t = new Ref2StringTransformer<Project>();
		return t.transformTo(this.project);
	}	

	@ApiResourceProperty(name = "project")
	public void setProjectKey(String project) {
		Ref2StringTransformer<Project> t = new Ref2StringTransformer<Project>();
		this.project = t.transformFrom(project);
	}

	@ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
	public Ref<User> getOrganizer() {
		return organizer;
	}

	@ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
	public void setOrganizer(Ref<User> organizer) {
		this.organizer = organizer;
	}
	
	@ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
	public void setOrganizer(User organizer) {
		this.organizer = Ref.create(organizer);
	}
	
	@ApiResourceProperty(name = "organizer")
	public String getOrganizerKey() {
		Ref2StringTransformer<User> t = new Ref2StringTransformer<User>();
		return t.transformTo(this.organizer);
	}	

	@ApiResourceProperty(name = "organizer")
	public void setOrganizerKey(String organizer) {
		Ref2StringTransformer<User> t = new Ref2StringTransformer<User>();
		this.organizer = t.transformFrom(organizer);
	}

	public MeetingPoll getPoll() {
		return poll;
	}

	public void setPoll(MeetingPoll poll) {
		this.poll = poll;
	}

	public String getCalendarEventId() {
		return calendarEventId;
	}

	public void setCalendarEventId(String calendarEventId) {
		this.calendarEventId = calendarEventId;
	}

	public String getHangoutLink() {
		return hangoutLink;
	}

	public void setHangoutLink(String hangoutLink) {
		this.hangoutLink = hangoutLink;
	}

}
