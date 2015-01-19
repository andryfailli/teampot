package com.google.teampot;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Logger;

import com.google.teampot.util.AppHelper;

public class Config {

	private static Config instance;
	
	private static final Logger logger = Logger.getLogger(Config.class.getSimpleName());
		
	public static final String WEB_CLIENT_ID = "WEB_CLIENT_ID";
	public static final String WEB_CLIENT_SECRET = "WEB_CLIENT_SECRET";
	public static final String APPLICATION_NAME = "APPLICATION_NAME";
	public static final String APPS_DOMAIN = "APPS_DOMAIN";
	public static final String APPS_GROUP = "APPS_GROUP";
	public static final String PROJECT_PREFIX = "PROJECT_PREFIX";
	public static final String DEV_LOCALHOST_USER = "DEV_LOCALHOST_USER";
	public static final String TEAMPOT_ACCOUNT = "TEAMPOT_ACCOUNT";
	public static final String SERVICE_ACCOUNT_EMAIL = "SERVICE_ACCOUNT_EMAIL";
	public static final String SERVICE_ACCOUNT_PKCS12_FILE_PATH = "SERVICE_ACCOUNT_PKCS12_FILE_PATH";
	public static final String FEATURE_ANALYTICS = "FEATURE_ANALYTICS";
	
	public static final String VALUE_TRUE = "true";
	
	private Properties props;
	
	private Config() {
		this.props = new Properties();
		String propsFilePath = "WEB-INF/"+AppHelper.getAppId()+".properties";
		
		try {
			File propsFile = new File(propsFilePath);
			this.props.load(new FileInputStream(propsFile));
		} catch (FileNotFoundException e) {
			logger.severe("Config file "+propsFilePath+" not found.");
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	public static String get(String key) {
		if (instance==null) instance = new Config();
		return instance.props.getProperty(key);
	}

}
