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
		addEndpoint(new Endpoint(Endpoint.Type.VERSION, Server.Type.API, "v2/status/version", 0));
		addEndpoint(new Endpoint(Endpoint.Type.COLLECTIONS, Server.Type.API, "maps/%s", APPEND_DEFAULT));
		addEndpoint(new Endpoint(Endpoint.Type.COLLECTIONS_FOR_USER, Server.Type.API, "v2/maps/user/%s", APPEND_DEFAULT));
		addEndpoint(new Endpoint(Endpoint.Type.PLACE, Server.Type.API, "business/%s", APPEND_DEFAULT));
		addEndpoint(new Endpoint(Endpoint.Type.USER, Server.Type.API, "v2/user/%s", APPEND_DEFAULT));
	}
}
