package com.citymaps.mobile.android.config;

public class ApiVersion3 extends ApiBase {

	protected ApiVersion3(int apiVersion, String apiBuild) {
		super(apiVersion, apiBuild);

		// Once we have a version 3, add those endpoints here, for example:
		// addEndpoint(new Endpoint(Endpoint.Type.USER, Server.Type.API, "v3/user/%s", APPEND_DEFAULT));
	}
}
