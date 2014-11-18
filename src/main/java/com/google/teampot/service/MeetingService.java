package com.google.teampot.service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.teampot.dao.MeetingDAO;
import com.google.teampot.dao.TaskDAO;
import com.google.teampot.diff.visitor.EntityDiffVisitor;
import com.google.teampot.model.EntityDiff;
import com.google.teampot.model.Meeting;
import com.google.teampot.model.MeetingActivityEvent;
import com.google.teampot.model.MeetingActivityEventVerb;
import com.google.teampot.model.MeetingPollVote;
import com.google.teampot.model.Task;
import com.google.teampot.model.TaskActivityEvent;
import com.google.teampot.model.TaskActivityEventVerb;
import com.google.teampot.model.User;

import de.danielbechler.diff.ObjectDifferBuilder;
import de.danielbechler.diff.node.DiffNode;

public class MeetingService{

	private static MeetingService instance;
	
	private MeetingDAO dao; 
	private static ActivityEventService activityEventService = ActivityEventService.getInstance();
	
	private MeetingService() {
		this.dao = new MeetingDAO();
	}
	
	public static MeetingService getInstance() {
		if (instance == null) instance = new MeetingService();
		return instance;
	}
	
	public List<Meeting> list(String projectId) {
		return dao.list(projectId);
	}
	
	public List<Meeting> list() {
		return dao.list();
	}
	
	public Meeting get(String key){
		return dao.get(key);
	}
	
	public void save(Meeting entity){
		this.save(entity,null);
	}
	
	public void save(Meeting entity, User actor){
		MeetingActivityEvent activtyEvent = new MeetingActivityEvent();
		if (entity.getId() == null) {
			activtyEvent.setVerb(MeetingActivityEventVerb.CREATE);
		} else {
			
			Meeting oldEntity = dao.get(entity.getKey());
			
			activtyEvent.setVerb(MeetingActivityEventVerb.EDIT);			
			
			DiffNode diffs = ObjectDifferBuilder.buildDefault().compare(entity, oldEntity);
			if (diffs.hasChanges()) {
				Map<String,EntityDiff> entityDiffs = new LinkedHashMap<String,EntityDiff>();
				diffs.visit(new EntityDiffVisitor(entity, oldEntity,entityDiffs));
				activtyEvent.setDiffs(entityDiffs);
			}

		}
		dao.save(entity);
		activtyEvent.setMeeting(entity);
		activtyEvent.setActor(actor);
		activityEventService.registerActivityEvent(activtyEvent);
	}
	
	public void remove(String key){		
		this.remove(key,null);
	}
	
	public void remove(String key, User actor){
		Meeting entity = dao.get(key);
		activityEventService.registerActivityEvent(new MeetingActivityEvent(entity,actor, MeetingActivityEventVerb.DELETE));
		dao.remove(key);
	}
	
	public void pollVote(Meeting meeting, MeetingPollVote vote) {
		
		int voteIndex = -1;
		
		for (int i=0; i<meeting.getPoll().getVotes().size(); i++) {
			MeetingPollVote curVote = meeting.getPoll().getVotes().get(i);
			if (curVote.getUser().equivalent(vote.getUser()) && curVote.getProposedDate().equals(vote.getProposedDate()))
				voteIndex = i;
		}
		
		if (voteIndex>=0) meeting.getPoll().getVotes().remove(voteIndex);
			
		meeting.getPoll().getVotes().add(vote);
		
		MeetingActivityEvent activtyEvent = new MeetingActivityEvent();
		activtyEvent.setMeeting(meeting);
		activtyEvent.setActor(vote.getUser());
		activtyEvent.setVerb(MeetingActivityEventVerb.VOTE);
		activityEventService.registerActivityEvent(activtyEvent);
		
		dao.save(meeting);
	}

}
