package com.citymaps.mobile.android.config;

import java.util.HashMap;
import java.util.Map;

public abstract class Api {

	public static Api newInstance(Environment environment, int apiVersion, String apiBuild) {
		if (apiVersion >= 3) {
			return new ApiV3(environment, apiVersion, apiBuild);
		} else {
			return new ApiBase(environment, apiVersion, apiBuild);
		}
	}

	private Environment mEnvironment;

	private int mApiVersion;

	private String mApiBuild;

	private Map<Endpoint.Type, Endpoint> mEndpointMap;

	protected Api(Environment environment, int apiVersion, String apiBuild) {
		mEnvironment = environment;
		mApiVersion = apiVersion;
		mApiBuild = apiBuild;
		mEndpointMap = new HashMap<Endpoint.Type, Endpoint>(Endpoint.Type.values().length);
		addEndpoints(environment, apiVersion, apiBuild);
	}

	protected void addEndpoint(Endpoint endpoint) {
		if (endpoint != null) {
			mEndpointMap.put(endpoint.getType(), endpoint);
		}
	}

	abstract void addEndpoints(Environment environment, int apiVersion, String apiBuild);

	public int getApiVersion() {
		return mApiVersion;
	}

	public String getApiBuild() {
		return mApiBuild;
	}

	protected Endpoint getEndpoint(Endpoint.Type type) {
		return mEndpointMap.get(type);
	}

	public Environment getEnvironment() {
		return mEnvironment;
	}
}
