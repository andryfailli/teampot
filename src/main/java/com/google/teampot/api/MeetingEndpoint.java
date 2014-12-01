package com.google.teampot.api;

import java.util.Date;
import java.util.List;

import com.google.teampot.api.exception.EntityNotFoundException;
import com.google.teampot.api.exception.MeetingPollPastException;
import com.google.teampot.model.Meeting;
import com.google.teampot.model.MeetingPollVote;
import com.google.teampot.service.MeetingService;
import com.google.teampot.service.UserService;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiMethod.HttpMethod;
import com.google.api.server.spi.config.Named;
import com.google.api.server.spi.response.UnauthorizedException;
import com.google.appengine.api.oauth.OAuthRequestException;

public class MeetingEndpoint extends BaseEndpoint {

	private static MeetingService meetingService = MeetingService.getInstance();
	private static UserService userService = UserService.getInstance();
	
	@ApiMethod(
		name = "meeting.list",
		path = "meeting",
		httpMethod = HttpMethod.GET
	)
	public List<Meeting> list(com.google.appengine.api.users.User gUser, @Named("project") String projectId) throws OAuthRequestException, UnauthorizedException {
		userService.ensureEnabled(gUser);
		
		return meetingService.list(projectId);
	}
	
	@ApiMethod(
		name = "meeting.get", 
		path = "meeting/{id}",
		httpMethod = HttpMethod.GET
	)
	public Meeting get(@Named("id") String id, com.google.appengine.api.users.User gUser) throws OAuthRequestException,EntityNotFoundException, UnauthorizedException {
		userService.ensureEnabled(gUser);
		
		Meeting entity = meetingService.get(id);
		if (entity != null)
			return entity;
		else
			throw new EntityNotFoundException(id);
	}
	
	@ApiMethod(
		name = "meeting.save",
		path = "meeting",
		httpMethod = HttpMethod.POST
	)
	public Meeting save(Meeting entity, com.google.appengine.api.users.User gUser) throws OAuthRequestException, UnauthorizedException {
		userService.ensureEnabled(gUser);
		
		meetingService.save(entity, userService.getUser(gUser));
		return entity;
	}
	
	@ApiMethod(
		name = "meeting.remove",
		path = "meeting/{id}",
		httpMethod = HttpMethod.DELETE
	)
	public void remove(@Named("id") String id, com.google.appengine.api.users.User gUser) throws OAuthRequestException, UnauthorizedException {
		userService.ensureEnabled(gUser);
		meetingService.remove(id, userService.getUser(gUser));
	}
	
	@ApiMethod(
		name = "meeting.pollVote",
		path = "meeting/{id}/poll/vote",
		httpMethod = HttpMethod.POST
	)
	public Meeting pollVote(@Named("id") String id, @Named("proposedDate") Date proposedDate, @Named("result") boolean result, com.google.appengine.api.users.User gUser) throws OAuthRequestException, MeetingPollPastException, EntityNotFoundException, UnauthorizedException {
		userService.ensureEnabled(gUser);
		
		Meeting meeting = meetingService.get(id);
		
		if (meeting == null) throw new EntityNotFoundException(id);
		if (meeting.isPast()) throw new MeetingPollPastException(id);
		
		MeetingPollVote vote = new MeetingPollVote();
		vote.setUser(userService.getUser(gUser));
		vote.setProposedDate(proposedDate);
		vote.setResult(result);
		
		meetingService.pollVote(meeting, vote);
		return meeting;
	}
	
}