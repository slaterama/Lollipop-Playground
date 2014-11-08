package com.citymaps.mobile.android.config;

public class ApiV3 extends ApiBase {

	protected ApiV3(Environment environment, int apiVersion, String apiBuild) {
		super(environment, apiVersion, apiBuild);

		// Once we have a version 3, add those endpoints here, for example:
		// addEndpoint(new Endpoint(Endpoint.Type.USER, Server.Type.API, "v3/user/%s", APPEND_DEFAULT));
	}
}
