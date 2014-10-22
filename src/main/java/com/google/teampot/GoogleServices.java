package com.google.teampot;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.List;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;

public class GoogleServices {

	private static GoogleServices instance;

	private HttpTransport httpTransport;
	private JacksonFactory jsonFactory;

	private GoogleServices() {
		this.httpTransport = new NetHttpTransport();
		this.jsonFactory = new JacksonFactory();
	}

	public static GoogleServices getInstance() {
		if (instance == null) instance = new GoogleServices();
		return instance;
	}

	private static GoogleCredential getCredential(String userEmail,List<String> scopes) throws GeneralSecurityException, IOException {
		return new GoogleCredential.Builder()
			.setTransport(GoogleServices.getInstance().httpTransport)
			.setJsonFactory(GoogleServices.getInstance().jsonFactory)
			.setServiceAccountId(Config.get(Config.SERVICE_ACCOUNT_EMAIL))
			.setServiceAccountScopes(scopes)
			.setServiceAccountUser(userEmail)
			.setServiceAccountPrivateKeyFromP12File(new File(Config.get(Config.SERVICE_ACCOUNT_PKCS12_FILE_PATH)))
			.build();
	}
	
	public static Drive getDriveService(String userEmail)	throws GeneralSecurityException, IOException {
		return new Drive.Builder(GoogleServices.getInstance().httpTransport, GoogleServices.getInstance().jsonFactory, null)
			.setHttpRequestInitializer(GoogleServices.getCredential(userEmail,Arrays.asList(DriveScopes.DRIVE)))
			.build();
	}

}
