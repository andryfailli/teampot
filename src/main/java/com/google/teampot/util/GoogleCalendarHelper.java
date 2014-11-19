package com.google.teampot.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;

public class GoogleCalendarHelper {

	public GoogleCalendarHelper() {
		// TODO Auto-generated constructor stub
	}
	
	public static void setAllDayEvent(Date date, Event event) {
		
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	    String startDateStr = dateFormat.format(date);
	    String endDateStr = dateFormat.format(new Date(date.getTime()+86400000));

	    // Out of the 6 methods for creating a DateTime object with no time element, only the String version works
	    DateTime startDateTime = new DateTime(startDateStr);
	    DateTime endDateTime = new DateTime(endDateStr);
	    
	    EventDateTime startEventDateTime = new EventDateTime();
		startEventDateTime.setDate(startDateTime);
		
		EventDateTime endEventDateTime = new EventDateTime();
		endEventDateTime.setDate(endDateTime);
	    
		
		event.setStart(startEventDateTime);
		event.setEnd(endEventDateTime);
		
	}

}
