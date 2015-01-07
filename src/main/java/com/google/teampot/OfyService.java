package com.google.teampot;

import com.google.appengine.api.datastore.ReadPolicy.Consistency;
import com.google.teampot.model.ActivityEvent;
import com.google.teampot.model.Meeting;
import com.google.teampot.model.MeetingActivityEvent;
import com.google.teampot.model.MemberActivityEvent;
import com.google.teampot.model.MetricsSnapshot;
import com.google.teampot.model.Project;
import com.google.teampot.model.ProjectActivityEvent;
import com.google.teampot.model.Task;
import com.google.teampot.model.TaskActivityEvent;
import com.google.teampot.model.User;
import com.google.teampot.model.UserActivityEvent;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyFactory;
import com.googlecode.objectify.ObjectifyService;

public class OfyService {
	
    static {
    	factory().register(ActivityEvent.class);
    	factory().register(Meeting.class);
    	factory().register(MeetingActivityEvent.class);
    	factory().register(Project.class);
    	factory().register(Task.class);
    	factory().register(TaskActivityEvent.class);
    	factory().register(User.class);
    	factory().register(UserActivityEvent.class);
    	factory().register(ProjectActivityEvent.class);
    	factory().register(MemberActivityEvent.class);
    	factory().register(MetricsSnapshot.class);
    }

    public static Objectify ofy() {
        return ObjectifyService.ofy()
			.consistency(Consistency.STRONG);
    }

    public static ObjectifyFactory factory() {
        return ObjectifyService.factory();
    }
    
}