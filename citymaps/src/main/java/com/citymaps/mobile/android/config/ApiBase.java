package com.citymaps.mobile.android.config;

public class ApiBase extends Api {

	protected ApiBase(int apiVersion, String apiBuild) {
		super(apiVersion, apiBuild);
		addEndpoint(new Endpoint(Endpoint.Type.COLLECTIONS, Server.Type.API, "maps/%s", APPEND_DEFAULT));
		addEndpoint(new Endpoint(Endpoint.Type.COLLECTIONS_FOR_USER, Server.Type.API, "v2/maps/user/%s", APPEND_DEFAULT));
		addEndpoint(new Endpoint(Endpoint.Type.PLACE, Server.Type.API, "business/%s", APPEND_DEFAULT));
		addEndpoint(new Endpoint(Endpoint.Type.USER, Server.Type.API, "v2/user/%s", APPEND_DEFAULT));
	}
}
