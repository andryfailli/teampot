package com.google.teampot;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class Config {

	private static Config instance;
		
	public static final String BACKEND_CLIENT_ID = "BACKEND_CLIENT_ID";
	public static final String BACKEND_CLIENT_SECRET = "BACKEND_CLIENT_SECRET";
	public static final String APPLICATION_NAME = "APPLICATION_NAME";
	public static final String APPS_DOMAIN = "APPS_DOMAIN";
	public static final String PROJECT_PREFIX = "PROJECT_PREFIX";
	
	private Properties props;
	
	private Config() {
		this.props = new Properties();
		
		try {
			File propsFile = new File("WEB-INF/app.properties");
			this.props.load(new FileInputStream(propsFile));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
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
