package com.google.teampot;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.api.services.admin.directory.Directory;
import com.google.api.services.admin.directory.DirectoryScopes;
import com.google.api.services.bigquery.Bigquery;
import com.google.api.services.bigquery.BigqueryScopes;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.drive.Drive;
import com.google.api.services.groupssettings.Groupssettings;
import com.google.api.services.groupssettings.GroupssettingsScopes;
import com.google.api.services.oauth2.Oauth2;
import com.google.api.services.oauth2.model.Tokeninfo;
import com.google.api.services.plus.Plus;
import com.google.appengine.api.oauth.OAuthRequestException;
import com.google.appengine.api.utils.SystemProperty;
import com.google.teampot.dao.UserDAO;
import com.google.teampot.model.User;
import com.google.teampot.service.UserService;

public class GoogleServices {

	private static GoogleServices instance;
	
	private static final Logger logger = Logger.getLogger(GoogleServices.class.getSimpleName());

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
	
	private static GoogleCredential.Builder getCredentialBuilder() {
		return new GoogleCredential.Builder()
			.setTransport(GoogleServices.getInstance().httpTransport)
			.setJsonFactory(GoogleServices.getInstance().jsonFactory)
			.setClientSecrets(Config.get(Config.WEB_CLIENT_ID), Config.get(Config.WEB_CLIENT_SECRET));
	}

	public static GoogleCredential getCredentialFromOneTimeCode(String userEmail,String code) throws IOException, OAuthRequestException {
		
	    // https://developers.google.com/+/web/signin/server-side-flow

		HttpTransport httpTransport = GoogleServices.getInstance().httpTransport;
		JsonFactory jsonFactory = GoogleServices.getInstance().jsonFactory;
		String clientId = Config.get(Config.WEB_CLIENT_ID);
		String clientSecret = Config.get(Config.WEB_CLIENT_SECRET);
		
	    // Upgrade the authorization code into an access and refresh token.
	    GoogleTokenResponse tokenResponse = new GoogleAuthorizationCodeTokenRequest(httpTransport, jsonFactory, clientId, clientSecret, code, "postmessage").execute();
	   
	    // Create a credential representation of the token data.
	    GoogleCredential credential = GoogleServices.getCredentialBuilder().build().setFromTokenResponse(tokenResponse);

	    // Check that the token is valid.
	    Oauth2 oauth2 = new Oauth2.Builder(httpTransport, jsonFactory, credential).setApplicationName(Config.get(Config.APPLICATION_NAME)).build();
	    Tokeninfo tokenInfo = oauth2.tokeninfo().setAccessToken(credential.getAccessToken()).execute();
	    
	    // If there was an error in the token info, abort.
	    if (tokenInfo.containsKey("error")) throw new OAuthRequestException(tokenInfo.get("error").toString());

	    // Make sure the token we got is for the intended user.
	    if (SystemProperty.environment.value()!=SystemProperty.Environment.Value.Development && tokenInfo.getUserId().equals(userEmail)) throw new OAuthRequestException("Token's user ID doesn't match given user ID.");
	    
	    // Make sure the token we got is for our app.
	    if (!tokenInfo.getIssuedTo().equals(clientId)) throw new OAuthRequestException("Token's client ID does not match app's.");
	    
	    return credential;
	}
	
	private static GoogleCredential getCredential(User user) throws IOException {
		
		GoogleCredential credential = GoogleServices.getCredentialBuilder().build();
		
		credential.setAccessToken(user.getAccessToken());
		credential.setRefreshToken(user.getRefreshToken());
		credential.setExpirationTimeMilliseconds(user.getCredentialExpirationTimestamp());
		
		if (credential.getExpiresInSeconds() == null || credential.getExpiresInSeconds()<=0) {
			logger.info("Refreshing access token...");
			credential.refreshToken();
			logger.info("Access token refreshed!");
			
			user.setCredential(credential);
			new UserDAO().save(user);
		} else {
			logger.info("Access token expires on "+credential.getExpirationTimeMilliseconds()+" (in "+credential.getExpiresInSeconds()+"s)");
		}
		
		return credential;
	}
	
	private static GoogleCredential getCredentialDomainWide(List<String> scopes) throws GeneralSecurityException, IOException {
		return getCredentialDomainWide(Config.get(Config.TEAMPOT_ACCOUNT),scopes);
	}
	
	private static GoogleCredential getCredentialDomainWide(String userEmail, List<String> scopes) throws GeneralSecurityException, IOException {
		return new GoogleCredential.Builder()
	      .setTransport(GoogleServices.getInstance().httpTransport)
	      .setJsonFactory(GoogleServices.getInstance().jsonFactory)
	      .setServiceAccountId(Config.get(Config.SERVICE_ACCOUNT_EMAIL))
	      .setServiceAccountScopes(scopes)
	      .setServiceAccountUser(userEmail)
	      .setServiceAccountPrivateKeyFromP12File(new java.io.File(Config.get(Config.SERVICE_ACCOUNT_PKCS12_FILE_PATH)))
	      .build();
	}

	
	
	
	public static Drive getDriveService(User user)	throws GeneralSecurityException, IOException {
		return new Drive.Builder(GoogleServices.getInstance().httpTransport, GoogleServices.getInstance().jsonFactory, null)
			.setHttpRequestInitializer(GoogleServices.getCredential(user))
			.setApplicationName(Config.get(Config.APPLICATION_NAME))
			.build();
	}
	
	
	public static Directory getDirectoryService(User user)	throws GeneralSecurityException, IOException {
		return new Directory.Builder(GoogleServices.getInstance().httpTransport, GoogleServices.getInstance().jsonFactory, null)
			.setHttpRequestInitializer(GoogleServices.getCredential(user))
			.setApplicationName(Config.get(Config.APPLICATION_NAME))
			.build();
	}
		
	public static Directory getDirectoryServiceDomainWide()	throws GeneralSecurityException, IOException {
		return new Directory.Builder(GoogleServices.getInstance().httpTransport, GoogleServices.getInstance().jsonFactory, null)
			.setHttpRequestInitializer(GoogleServices.getCredentialDomainWide(Arrays.asList(DirectoryScopes.ADMIN_DIRECTORY_GROUP,DirectoryScopes.ADMIN_DIRECTORY_USER_READONLY)))
			.setApplicationName(Config.get(Config.APPLICATION_NAME))
			.build();
	}
	
	
	public static Groupssettings getGroupssettingsService(User user)	throws GeneralSecurityException, IOException {
		return new Groupssettings.Builder(GoogleServices.getInstance().httpTransport, GoogleServices.getInstance().jsonFactory, null)
			.setHttpRequestInitializer(GoogleServices.getCredential(user))
			.setApplicationName(Config.get(Config.APPLICATION_NAME))
			.build();
	}
	
	public static Groupssettings getGroupssettingsDomainWide()	throws GeneralSecurityException, IOException {
		return new Groupssettings.Builder(GoogleServices.getInstance().httpTransport, GoogleServices.getInstance().jsonFactory, null)
			.setHttpRequestInitializer(GoogleServices.getCredentialDomainWide(Arrays.asList(GroupssettingsScopes.APPS_GROUPS_SETTINGS)))
			.setApplicationName(Config.get(Config.APPLICATION_NAME))
			.build();
	}
	
	
	public static Calendar getCalendarService(User user) throws GeneralSecurityException, IOException {
		return new Calendar.Builder(GoogleServices.getInstance().httpTransport, GoogleServices.getInstance().jsonFactory, null)
			.setHttpRequestInitializer(GoogleServices.getCredential(user))
			.setApplicationName(Config.get(Config.APPLICATION_NAME))
			.build();
	}
	
	public static Calendar getCalendarServiceDomainWide() throws GeneralSecurityException, IOException {
		return new Calendar.Builder(GoogleServices.getInstance().httpTransport, GoogleServices.getInstance().jsonFactory, null)
			.setHttpRequestInitializer(GoogleServices.getCredentialDomainWide(Arrays.asList(CalendarScopes.CALENDAR)))
			.setApplicationName(Config.get(Config.APPLICATION_NAME))
			.build();
	}
	public static Calendar getCalendarServiceDomainWide(User user) throws GeneralSecurityException, IOException {
		return new Calendar.Builder(GoogleServices.getInstance().httpTransport, GoogleServices.getInstance().jsonFactory, null)
			.setHttpRequestInitializer(GoogleServices.getCredentialDomainWide(user.getEmail(),Arrays.asList(CalendarScopes.CALENDAR)))
			.setApplicationName(Config.get(Config.APPLICATION_NAME))
			.build();
	}
	
	public static Bigquery getBigqueryServiceDomainWide() throws GeneralSecurityException, IOException {
		return new Bigquery.Builder(GoogleServices.getInstance().httpTransport, GoogleServices.getInstance().jsonFactory, null)
			.setHttpRequestInitializer(GoogleServices.getCredentialDomainWide(Arrays.asList(BigqueryScopes.BIGQUERY)))
			.setApplicationName(Config.get(Config.APPLICATION_NAME))
			.build();
	}

}
