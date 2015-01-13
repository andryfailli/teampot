package com.google.teampot.servlet.task;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.teampot.GoogleServices;
import com.google.teampot.model.Project;
import com.google.teampot.service.ProjectService;

public class WatchFolderChangesServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	
	private static final Logger logger = Logger.getLogger(WatchFolderChangesServlet.class.getSimpleName());

	@Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException { 
		String projectKey = req.getParameter("project");
		Project project = ProjectService.getInstance().get(projectKey);
		try {
			ProjectService.getInstance().watchFolderChanges(GoogleServices.getDriveService(project.getOwner().get()), project);
		} catch (GeneralSecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
	}
	

}
