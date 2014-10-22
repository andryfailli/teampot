package com.google.teampot;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class Config {

	private static Config instance;
		
	public static final String SERVICE_ACCOUNT_EMAIL = "SERVICE_ACCOUNT_EMAIL";
	public static final String SERVICE_ACCOUNT_PKCS12_FILE_PATH = "SERVICE_ACCOUNT_PKCS12_FILE_PATH";
	
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
