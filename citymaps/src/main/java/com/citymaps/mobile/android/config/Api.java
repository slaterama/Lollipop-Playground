package com.citymaps.mobile.android.config;

import com.citymaps.mobile.android.model.vo.ApiBuild;
import com.citymaps.mobile.android.util.LogEx;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public abstract class Api {

	public static Api newInstance(Environment environment, ApiBuild apiBuild) {
		int versionNumber = apiBuild.getVersionNumber();
		if (versionNumber >= 3) {
			return new ApiVersion3(environment, apiBuild);
		} else {
			return new ApiBase(environment, apiBuild);
		}
	}

	protected Environment mEnvironment;

	protected ApiBuild mApiBuild;

	private Map<Endpoint, String> mEndpointMap;

	public Api(Environment environment, ApiBuild apiBuild) {
		super();
		mEnvironment = environment;
		mApiBuild = apiBuild;
		mEndpointMap = new HashMap<Endpoint, String>();
	}

	protected void configureEndpoint(Endpoint endpoint, String endpointString) {
		mEndpointMap.put(endpoint, endpointString);
	}

	public String getUrlString(Endpoint endpoint, Object... args) {
		String urlString = null;
		try {
			String endpointString = mEndpointMap.get(endpoint);
			Host host = endpoint.getHost();
			URL url = mEnvironment.buildUrl(host, endpointString, args);
			urlString = url.toString();
		} catch (MalformedURLException e) {
			if (LogEx.isLoggable(LogEx.ERROR)) {
				LogEx.e(e.getMessage(), e);
			}
		} catch (NullPointerException e) {
			if (LogEx.isLoggable(LogEx.ERROR)) {
				LogEx.e(e.getMessage(), e);
			}
		}
		return urlString;
	}
}
