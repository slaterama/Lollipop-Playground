package com.citymaps.mobile.android.config;

import com.citymaps.mobile.android.model.vo.ApiBuild;

public class ApiBase extends Api {

	public ApiBase(ApiBuild apiBuild) {
		super(apiBuild);
	}

	@Override
	public void defineEndpoints(EndpointMap endpointMap) {
		endpointMap.put(Endpoint.BUILD, "v2/status/version");
		endpointMap.put(Endpoint.USER, "user/%s");
		endpointMap.put(Endpoint.PLACE, "business/%s");
		endpointMap.put(Endpoint.COLLECTIONS, "maps/%s");
		endpointMap.put(Endpoint.COLLECTIONS_FOR_USER, "v2/maps/user/%s");
	}
}
