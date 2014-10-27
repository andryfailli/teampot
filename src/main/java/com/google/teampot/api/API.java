package com.google.teampot.api;

import com.google.apphosting.api.ApiProxy;
import com.google.apphosting.api.ApiProxy.Environment;
import com.google.teampot.Constants;

public class API {

	private API() {
		// TODO Auto-generated constructor stub
	}
	
	public static String getBaseUrl() {
		return "https://"+API.getBaseUrlWithoutSchema();
	}
	
	public static String getBaseUrlWithoutSchema() {
		Environment environment = ApiProxy.getCurrentEnvironment();
		return environment.getAppId()+".appspot.com"+API.getBaseUrlWithoutHostAndSchema();
	}
	
	public static String getBaseUrlWithoutHostAndSchema() {
		return "/_ah/api/teampot/"+Constants.CURRENT_API_VERSION;
	}

}
