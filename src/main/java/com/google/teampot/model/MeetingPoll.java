package com.google.teampot.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.stringifier.RefUserStringifier;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Stringify;

public class MeetingPoll {
	
	private List<Date> proposedDates;
	
	@Stringify(RefUserStringifier.class)
	private Map<Ref<User>,Date> votes;
	
	private Date startDate;
	
	private Date endDate;
	
	public MeetingPoll() {
		this.proposedDates = new ArrayList<Date>();
		this.votes = new LinkedHashMap<Ref<User>, Date>();
	}

	public List<Date> getProposedDates() {
		return proposedDates;
	}

	public void setProposedDates(List<Date> proposedDates) {
		this.proposedDates = proposedDates;
	}

	public Map<Ref<User>, Date> getVotes() {
		return votes;
	}

	public void setVotes(Map<Ref<User>, Date> votes) {
		this.votes = votes;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	
}
