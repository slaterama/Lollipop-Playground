package com.citymaps.mobile.android.config;

import java.util.HashMap;
import java.util.Map;

public abstract class EndpointManager {

	public static final int APPEND_TIMESTAMP = 0x00000001;
	public static final int APPEND_ANDROID_VERSION = 0x00000002;
	public static final int APPEND_USER_ID = 0x00000004;
	public static final int APPEND_DEVICE_ID = 0x00000008;
	public static final int APPEND_SECRET = 0x00000010;

	public static final int APPEND_CITYMAPS_TOKEN = 0x00000020;
	public static final int APPEND_ENDPOINT_VERSION = 0x00000040;

	public static final int APPEND_DEFAULT_WITHOUT_TOKEN = APPEND_TIMESTAMP|APPEND_ANDROID_VERSION|
			APPEND_USER_ID|APPEND_DEVICE_ID|APPEND_SECRET;

	public static final int APPEND_DEFAULT = APPEND_DEFAULT_WITHOUT_TOKEN|APPEND_CITYMAPS_TOKEN;

	private Map<Endpoint.Type, Endpoint> mEndpointMap;

	public EndpointManager() {
		mEndpointMap = new HashMap<Endpoint.Type, Endpoint>(Endpoint.Type.values().length);
	}

	protected void addEndpoint(Endpoint endpoint) {
		if (endpoint != null) {
			mEndpointMap.put(endpoint.getType(), endpoint);
		}
	}

	protected Endpoint getEndpoint(Endpoint.Type type) {
		return mEndpointMap.get(type);
	}
}
