package com.google.teampot.util;

import com.google.appengine.api.utils.SystemProperty;
import com.google.apphosting.api.ApiProxy;
import com.google.apphosting.api.ApiProxy.Environment;

public class AppHelper {

	public AppHelper() {
		
	}
	
	public static String getBaseUrl() {
		return "https://"+AppHelper.getBaseUrlWithoutSchema();
	}
	
	public static String getBaseUrlWithoutSchema() {
		if (SystemProperty.environment.value()!=SystemProperty.Environment.Value.Development) {
			Environment environment = ApiProxy.getCurrentEnvironment();
			
			String appFullId = environment.getAppId();
			int tildeIndex = appFullId.indexOf("~");
			
			String appId = tildeIndex>-1 ? appFullId.substring(tildeIndex+1) : appFullId;
			
			return appId+".appspot.com";
		} else {
			return "127.0.0.1:8888";
		}
	}

}