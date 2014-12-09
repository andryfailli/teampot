package com.google.teampot.model;

import java.util.Date;

import com.google.api.server.spi.config.AnnotationBoolean;
import com.google.api.server.spi.config.ApiResourceProperty;
import com.google.teampot.transformer.Date2TimestampTransformer;

public class MeetingPollProposedDate {
	
	private Date start;
	
	private Date end;
	
	public MeetingPollProposedDate() {
		
	}

	@ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
	public Date getStart() {
		return start;
	}

	@ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
	public void setStart(Date start) {
		this.start = start;
	}
	
	@ApiResourceProperty(name = "start")
	public Long getStartTimestamp() {
		Date2TimestampTransformer t = new Date2TimestampTransformer();
		return t.transformTo(start);
	}

	@ApiResourceProperty(name = "start")
	public void setStartTimestamp(Long timestamp) {
		Date2TimestampTransformer t = new Date2TimestampTransformer();
		this.start = t.transformFrom(timestamp);
	}
	
	@ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
	public Date getEnd() {
		return end;
	}

	@ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
	public void setEnd(Date end) {
		this.end = end;
	}
	
	@ApiResourceProperty(name = "end")
	public Long getEndTimestamp() {
		Date2TimestampTransformer t = new Date2TimestampTransformer();
		return t.transformTo(end);
	}

	@ApiResourceProperty(name = "end")
	public void setEndTimestamp(Long end) {
		Date2TimestampTransformer t = new Date2TimestampTransformer();
		this.end = t.transformFrom(end);
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((end == null) ? 0 : end.hashCode());
		result = prime * result + ((start == null) ? 0 : start.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MeetingPollProposedDate other = (MeetingPollProposedDate) obj;
		if (end == null) {
			if (other.end != null)
				return false;
		} else if (!end.equals(other.end))
			return false;
		if (start == null) {
			if (other.start != null)
				return false;
		} else if (!start.equals(other.start))
			return false;
		return true;
	}
	
}
