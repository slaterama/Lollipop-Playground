package com.citymaps.mobile.android.config;

import com.citymaps.mobile.android.model.vo.ApiBuild;

public class ApiBase extends Api {

	public ApiBase(Environment environment, ApiBuild apiBuild) {
		super(environment, apiBuild);

		// CONFIG is a rare case where the environment actually matters
		Environment.Type type = mEnvironment.getType();
		if (Environment.Type.DEVELOPMENT.equals(type)) {
			configureEndpoint(Endpoint.CONFIG, "riak/appconfig/android_config_dev.json");
		} else {
			configureEndpoint(Endpoint.CONFIG, "riak/appconfig/android_config.json");
		}

		configureEndpoint(Endpoint.BUILD, "v2/status/version");
		configureEndpoint(Endpoint.USER, "user/%s");
		configureEndpoint(Endpoint.PLACE, "business/%s");
		configureEndpoint(Endpoint.COLLECTIONS, "maps/%s");
		configureEndpoint(Endpoint.COLLECTIONS_FOR_USER, "v2/maps/user/%s");
	}
}
