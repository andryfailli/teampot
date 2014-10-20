package com.google.teampot.api;

import com.google.api.server.spi.config.AnnotationBoolean;
import com.google.api.server.spi.config.Api;
import com.google.teampot.Constants;

@Api(
	name = "teampot",
	version = "v1",
	defaultVersion = AnnotationBoolean.TRUE,
	scopes = {Constants.EMAIL_SCOPE},
	clientIds = {Constants.WEB_CLIENT_ID, Constants.API_EXPLORER_CLIENT_ID}
)
public class BaseEndpoint {

}
