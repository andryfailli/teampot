package com.google.teampot.api;

import com.google.api.server.spi.config.AnnotationBoolean;
import com.google.api.server.spi.config.Api;
import com.google.teampot.Constants;

@Api(
	name = "teampot",
	version = Constants.CURRENT_API_VERSION,
	defaultVersion = AnnotationBoolean.TRUE,
	scopes = {com.google.api.server.spi.Constant.API_EMAIL_SCOPE},
	clientIds = {Constants.WEB_CLIENT_ID, com.google.api.server.spi.Constant.API_EXPLORER_CLIENT_ID}
)
public class BaseEndpoint {

}
