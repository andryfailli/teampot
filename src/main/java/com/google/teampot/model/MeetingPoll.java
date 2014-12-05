package com.google.teampot.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.hsqldb.lib.HashMap;

import com.google.api.server.spi.config.AnnotationBoolean;
import com.google.api.server.spi.config.ApiResourceProperty;
import com.google.teampot.transformer.Date2TimestampTransformer;
import com.google.teampot.util.ValueComparator;

public class MeetingPoll {
	
	private List<Date> proposedDates;
	
	private List<MeetingPollVote> votes;
	
	private Date startDate;
	
	private Date endDate;
	
	public MeetingPoll() {
		this.proposedDates = new ArrayList<Date>();
		this.votes = new ArrayList<MeetingPollVote>();
	}

	@ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
	public List<Date> getProposedDates() {
		return proposedDates;
	}

	@ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
	public void setProposedDates(List<Date> proposedDates) {
		this.proposedDates = proposedDates;
	}
	
	@ApiResourceProperty(name = "proposedDates")
	public List<Long> getProposedDateTimestamps() {
		Date2TimestampTransformer t = new Date2TimestampTransformer();
		return t.transformTo(proposedDates);
	}

	@ApiResourceProperty(name = "proposedDates")
	public void setProposedDateTimestamps(List<Long> proposedDates) {
		Date2TimestampTransformer t = new Date2TimestampTransformer();
		this.proposedDates = t.transformFrom(proposedDates);
	}

	@ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
	public Date getStartDate() {
		return startDate;
	}

	@ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	
	@ApiResourceProperty(name = "startDate")
	public Long getStartDateTimestamp() {
		Date2TimestampTransformer t = new Date2TimestampTransformer();
		return t.transformTo(startDate);
	}

	@ApiResourceProperty(name = "startDate")
	public void setStartDateTimestamp(Long startDate) {
		Date2TimestampTransformer t = new Date2TimestampTransformer();
		this.startDate = t.transformFrom(startDate);
	}

	@ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
	public Date getEndDate() {
		return endDate;
	}

	@ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	
	@ApiResourceProperty(name = "endDate")
	public Long getEndDateTimestamp() {
		Date2TimestampTransformer t = new Date2TimestampTransformer();
		return t.transformTo(endDate);
	}

	@ApiResourceProperty(name = "endDate")
	public void setEndDateTimestamp(Long endDate) {
		Date2TimestampTransformer t = new Date2TimestampTransformer();
		this.endDate = t.transformFrom(endDate);
	}

	public List<MeetingPollVote> getVotes() {
		return votes;
	}

	public void setVotes(List<MeetingPollVote> votes) {
		this.votes = votes;
	}
	
	public boolean isStarted(){
		if (this.startDate != null)
			return (new Date()).after(this.startDate);
		else
			return false;
	}
	
	public boolean isEnded(){
		if (this.endDate != null)
			return (new Date()).after(this.endDate);
		else
			return false;
	}
	
	public boolean isOpen(){
		return this.isStarted() && !this.isEnded();
	}
	
	@ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
	public Date getPreferredDate() {
		Map<Date,Integer> unsortedDates = new LinkedHashMap<Date, Integer>();
		
		// init
		for (Date date : this.proposedDates) {
			unsortedDates.put(date, 0);
		}
		
		// count
		for (MeetingPollVote vote : this.votes) {
			if (vote.isResult() && unsortedDates.containsKey(vote.getProposedDate())) {
				Integer v = unsortedDates.get(vote.getProposedDate());
				unsortedDates.put(vote.getProposedDate(), v+1);
			}
		}
		
		// sort
		Map<Date,Integer> sortedDates = new TreeMap<Date,Integer>(new ValueComparator(unsortedDates));
		sortedDates.putAll(unsortedDates);
		
		Date preferredDate = sortedDates.keySet().iterator().next();
		if (preferredDate != null && unsortedDates.get(preferredDate)>0)
			return preferredDate;
		else 
			return null;
	}
	
	@ApiResourceProperty(name = "preferredDate")
	public Long getPreferredDateTimestamp() {
		Date2TimestampTransformer t = new Date2TimestampTransformer();
		return t.transformTo(this.getPreferredDate());
	}
	
}
