package com.citymaps.mobile.android.config;

import static com.citymaps.mobile.android.config.Endpoint.*;

public class ApiBase extends Api {

	private static String getConfigEndpoint(Environment environment) {
		switch (environment.getType()) {
			case DEVELOPMENT:
				return "riak/appconfig/android_config_dev.json";
			case PRODUCTION:
			default:
				return "riak/appconfig/android_config.json";
		}
	}

	protected ApiBase(Environment environment, int apiVersion, String apiBuild) {
		super(environment, apiVersion, apiBuild);
	}

	@Override
	void addEndpoints(Environment environment, int apiVersion, String apiBuild) {
		addEndpoint(new Endpoint(Endpoint.Type.CONFIG, Server.Type.ASSETS, getConfigEndpoint(environment), 0));
		addEndpoint(new Endpoint(Endpoint.Type.VERSION, "v2/status/version", 0));
		addEndpoint(new Endpoint(Endpoint.Type.COLLECTIONS, "maps/%s"));
		addEndpoint(new Endpoint(Endpoint.Type.COLLECTIONS_FOR_USER, "v2/maps/user/%s"));
		addEndpoint(new Endpoint(Endpoint.Type.PLACE, "business/%s"));
		addEndpoint(new Endpoint(Endpoint.Type.USER, "v2/user/%s"));
		addEndpoint(new Endpoint(Endpoint.Type.USER_LOGIN, "v2/user/login", APPEND_DEFAULT));
	}
}
