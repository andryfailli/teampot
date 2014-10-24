package com.google.teampot.model;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.github.slugify.Slugify;
import com.google.api.server.spi.config.AnnotationBoolean;
import com.google.api.server.spi.config.ApiResourceProperty;
import com.google.teampot.transformer.Ref2StringTransformer;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

@Entity
public class Project extends BaseEntity {

	@Id
	private Long id;
	
	//TODO: valutare se rimuovere bidi-ref
	private List<Ref<ActivityEvent>> activityEvents;
	
	private List<Ref<Meeting>> meetings;
	
	private List<Ref<Task>> tasks;
	
	private Set<Ref<User>> users;
	
	private String folder;
	
	private String name;
	
	private String machineName;
	
	private String description;
	
	private String groupEmail;
	
	private Ref<User> owner;

	public Project() {
		this.activityEvents = new ArrayList<Ref<ActivityEvent>>();
		this.meetings = new ArrayList<Ref<Meeting>>();
		this.tasks = new ArrayList<Ref<Task>>();
		this.users = new LinkedHashSet<Ref<User>>();
	}

	public Project(String name) {
		this();
		this.setName(name);
	}
	
	@ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
	public Long getId() {
		return id;
	}

	@ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
	public void setId(Long id) {
		this.id = id;
	}
	
	@Override
	@ApiResourceProperty(name = "id")
	public String getKey() {
		return Key.create(this.getClass(), this.getId()).getString();
	}

	@ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
	public List<Ref<ActivityEvent>> getActivityEvents() {
		return activityEvents;
	}
	
	public int getActivityEventsCount() {
		if (this.activityEvents != null)
			return this.activityEvents.size();
		else
			return 0;
	}

	@ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
	public void setActivityEvents(List<Ref<ActivityEvent>> activityEvents) {
		this.activityEvents = activityEvents;
	}

	@ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
	public List<Ref<Meeting>> getMeetings() {
		return meetings;
	}
	
	public int getMeetingsCount() {
		if (this.meetings != null)
			return this.meetings.size();
		else
			return 0;
	}

	@ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
	public void setMeetings(List<Ref<Meeting>> meetings) {
		this.meetings = meetings;
	}

	@ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
	public List<Ref<Task>> getTasks() {
		return tasks;
	}

	@ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
	public void setTasks(List<Ref<Task>> tasks) {
		this.tasks = tasks;
	}
	
	public int getTasksCount() {
		if (this.tasks != null)
			return this.tasks.size();
		else
			return 0;
	}

	@ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
	public Set<Ref<User>> getUsers() {
		return users;
	}

	@ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
	public void setUsers(Set<Ref<User>> users) {
		this.users = users;
	}
	
	public int getUsersCount() {
		if (this.users != null)
			return this.users.size();
		else
			return 0;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
    	Slugify sfy = new Slugify();
    	this.machineName = sfy.slugify(this.name.toLowerCase());
	}
	
	@ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
	public Ref<User> getOwner() {
		return owner;
	}
	
	@ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
	public void setOwner(Ref<User> owner) {
		this.owner = owner;
	}
	
	@ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
	public void setOwner(User owner) {
		this.owner = Ref.create(owner);
	}
	
	@ApiResourceProperty(name = "owner")
	public String getOwnerKey() {
		Ref2StringTransformer<User> t = new Ref2StringTransformer<User>();
		return t.transformTo(this.owner);
	}	

	@ApiResourceProperty(name = "owner")
	public void setOwnerKey(String owner) {
		Ref2StringTransformer<User> t = new Ref2StringTransformer<User>();
		this.owner = t.transformFrom(owner);
	}

	public String getFolder() {
		return folder;
	}

	public void setFolder(String folder) {
		this.folder = folder;
	}

	public String getMachineName() {
		return machineName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getGroupEmail() {
		return groupEmail;
	}

	public void setGroupEmail(String groupEmail) {
		this.groupEmail = groupEmail;
	}

}
