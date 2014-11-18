package com.google.teampot.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.api.server.spi.config.AnnotationBoolean;
import com.google.api.server.spi.config.ApiResourceProperty;

public class MeetingPoll {
	
	private List<Date> proposedDates;
	
	private List<MeetingPollVote> votes;
	
	private Date startDate;
	
	private Date endDate;
	
	public MeetingPoll() {
		this.proposedDates = new ArrayList<Date>();
		this.votes = new ArrayList<MeetingPollVote>();
	}

	public List<Date> getProposedDates() {
		return proposedDates;
	}

	public void setProposedDates(List<Date> proposedDates) {
		this.proposedDates = proposedDates;
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

	public List<MeetingPollVote> getVotes() {
		return votes;
	}

	public void setVotes(List<MeetingPollVote> votes) {
		this.votes = votes;
	}
	
}
