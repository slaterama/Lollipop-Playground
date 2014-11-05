package com.citymaps.mobile.android.config;

import com.citymaps.mobile.android.os.SoftwareVersion;



public class ApiBase extends Api {

	public ApiBase(Environment environment, int apiVersion, SoftwareVersion apiBuild) {
		super(environment, apiVersion, apiBuild);
	}

	@Override
	protected Endpoint createEndpoint(Endpoint.Type type) {
		switch (type) {
			case CONFIG:
				return new Endpoint(type, Server.Type.ASSETS, mEnvironment.getConfigEndpoint(), 0);
			case STATUS:
				return new Endpoint(type, Server.Type.API, "v2/status/version", 0);

			case COLLECTIONS:
				return new Endpoint(type, Server.Type.API, "maps/%s", Endpoint.APPEND_DEFAULT_TOKEN);
			case COLLECTIONS_FOR_USER:
				return new Endpoint(type, Server.Type.API, "v2/maps/user/%s", Endpoint.APPEND_DEFAULT_TOKEN);

			case PLACE:
				return new Endpoint(type, Server.Type.API, "business/%s", Endpoint.APPEND_DEFAULT_TOKEN);

			case USER:
				return new Endpoint(type, Server.Type.API, "user/%s", Endpoint.APPEND_DEFAULT_TOKEN);

			default:
				return null;
		}
	}
}
