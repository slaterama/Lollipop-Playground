package com.citymaps.mobile.android.confignew;

public abstract class Api extends EndpointManager {

	public static Api newInstance(int apiVersion, String apiBuild) {
		if (apiVersion >= 3) {
			return new ApiVersion3(apiVersion, apiBuild);
		} else {
			return new ApiBase(apiVersion, apiBuild);
		}
	}

	int mApiVersion;

	String mApiBuild;

	protected Api(int apiVersion, String apiBuild) {
		super();
		mApiVersion = apiVersion;
		mApiBuild = apiBuild;
	}

	public int getApiVersion() {
		return mApiVersion;
	}

	public String getApiBuild() {
		return mApiBuild;
	}
}
