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
	
}
