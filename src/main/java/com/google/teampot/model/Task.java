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
import com.googlecode.objectify.annotation.Parent;

@Entity
public class Task extends BaseEntity {

	@Id
	private Long id;
	
	@Parent
	private Ref<Project> project;
	
	private String title;
	
	private Text description;
	
	private int priority;
	
	private Ref<User> assignee;
	
	private boolean completed;
	
	private Date dueDate;
	
	private String dueDateCalendarEventId;
	
	private Date creationDate;
	
	private Set<String> files;
	
	private List<Ref<TaskComment>> comments;

	public Task() {
		this.files = new LinkedHashSet<String>();
		this.comments = new ArrayList<Ref<TaskComment>>();
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

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	@ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
	public Ref<User> getAssignee() {
		return assignee;
	}

	@ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
	public void setAssignee(Ref<User> assignee) {
		this.assignee = assignee;
	}
	
	@ApiResourceProperty(name = "assignee")
	public User getAssigneeEntity() {
		return this.assignee != null ? this.assignee.get() : null;
	}
	
	@ApiResourceProperty(name = "assignee")
	public void setAssigneeEntity(User assignee) {
		if (assignee != null) {
			this.assignee = Ref.create(assignee);
		} else {
			this.assignee = null;
		}
	}
	
	public boolean isAssigned() {
		return assignee != null;
	}

	public boolean isCompleted() {
		return completed;
	}

	public void setCompleted(boolean completed) {
		this.completed = completed;
	}

	public Date getDueDate() {
		return dueDate;
	}

	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
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
	public List<Ref<TaskComment>> getComments() {
		return comments;
	}

	@ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
	public void setComments(List<Ref<TaskComment>> comments) {
		this.comments = comments;
	}
	
	public int getCommentsCount() {
		if (this.comments != null)
			return this.comments.size();
		else
			return 0;
	}

	@ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
	public String getDueDateCalendarEventId() {
		return dueDateCalendarEventId;
	}

	@ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
	public void setDueDateCalendarEventId(String dueDateCalendarEventId) {
		this.dueDateCalendarEventId = dueDateCalendarEventId;
	}

}
