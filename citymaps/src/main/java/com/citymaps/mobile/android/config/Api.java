package com.citymaps.mobile.android.config;

import java.util.HashMap;
import java.util.Map;

public abstract class Api {

	public static Api newInstance(Environment environment, Version version) {
		switch (version) {
			case V3:
				return new ApiV3(environment, version);
			case V2:
			case V1:
			default:
				return new ApiBase(environment, version);
		}
	}

	private Environment mEnvironment;

	private Version mVersion;

	private String mApiBuild;

	private Map<Endpoint.Type, Endpoint> mEndpointMap;

	protected Api(Environment environment, Version version) {
		mEnvironment = environment;
		mVersion = version;
		mEndpointMap = new HashMap<Endpoint.Type, Endpoint>(Endpoint.Type.values().length);
		addEndpoints(environment, version);
	}

	protected void addEndpoint(Endpoint endpoint) {
		if (endpoint != null) {
			mEndpointMap.put(endpoint.getType(), endpoint);
		}
	}

	abstract void addEndpoints(Environment environment, Version version);

	public Version getVersion() {
		return mVersion;
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

	public static enum Version {
		V1("v1", "1.0.0"),
		V2("v2", "3.0.0"),
		V3("v3", "3.0.0-unknown");

		String mName;
		String mBuild;

		private Version(String name, String build) {
			mName = name;
			mBuild = build;
		}
	}
}
