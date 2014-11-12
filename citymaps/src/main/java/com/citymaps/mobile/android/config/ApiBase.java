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
		addEndpoint(new Endpoint(Type.TERMS_OF_SERVICE, Server.Type.CITYMAPS, "terms", APPEND_NONE));
		addEndpoint(new Endpoint(Type.PRIVACY_POLICY, Server.Type.CITYMAPS, "privacy", APPEND_NONE));
		addEndpoint(new Endpoint(Type.CONFIG, Server.Type.ASSETS, getConfigEndpoint(environment), APPEND_NONE));
		addEndpoint(new Endpoint(Type.VERSION, "v2/status/version", APPEND_NONE));
		addEndpoint(new Endpoint(Type.COLLECTIONS, "maps/%s"));
		addEndpoint(new Endpoint(Type.COLLECTIONS_FOR_USER, "v2/maps/user/%s"));
		addEndpoint(new Endpoint(Type.PLACE, "business/%s"));
		addEndpoint(new Endpoint(Type.USER, "v2/user/%s"));
		addEndpoint(new Endpoint(Type.USER_LOGIN, "v2/user/login", APPEND_DEFAULT));
		addEndpoint(new Endpoint(Type.USER_LOGIN_WITH_TOKEN, "v2/user/login?citymaps_token=%s", APPEND_DEFAULT));
		addEndpoint(new Endpoint(Type.USER_REGISTER, "v2/user/register?login=1", APPEND_DEFAULT));
	}
}
