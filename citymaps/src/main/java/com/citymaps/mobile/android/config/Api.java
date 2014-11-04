package com.citymaps.mobile.android.config;

import com.citymaps.mobile.android.model.vo.ApiBuild;

import java.util.HashMap;

public abstract class Api {

	public static Api newInstance(ApiBuild apiBuild) {
		int version = apiBuild.getVersion();
		if (version >= 3) {
			return new ApiVersion3(apiBuild);
		} else {
			return new ApiBase(apiBuild);
		}
	}

	private ApiBuild mApiBuild;

	private EndpointMap mEndpointMap;

	public Api(ApiBuild apiBuild) {
		super();
		mApiBuild = apiBuild;
		mEndpointMap = new EndpointMap();
		defineEndpoints(mEndpointMap);
	}

	public abstract void defineEndpoints(EndpointMap endpointMap);

	public String getEndpointString(Endpoint endpoint, Object... args) {
		String endpointString = null;
		String unformattedEndpointString = mEndpointMap.get(endpoint);
		if (unformattedEndpointString != null) {
			endpointString = String.format(unformattedEndpointString, args);
		}
		return endpointString;
	}

	protected static class EndpointMap extends HashMap<Endpoint, String> {}

	public static enum Endpoint {
		BUILD,
		USER,
		PLACE,
		COLLECTIONS,
		COLLECTIONS_FOR_USER
	}
}
