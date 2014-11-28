package com.google.teampot.api;

import com.google.teampot.Constants;
import com.google.teampot.util.AppHelper;

public class API {

	private API() {
		
	}
	
	public static String getBaseUrl() {
		return "https://"+API.getBaseUrlWithoutSchema();
	}
	
	public static String getBaseUrlWithoutSchema() {
		return AppHelper.getBaseUrlWithoutSchema()+API.getBaseUrlWithoutHostAndSchema();
	}
	
	public static String getBaseUrlWithoutHostAndSchema() {
		return "/_ah/api/teampot/"+Constants.CURRENT_API_VERSION;
	}

}
