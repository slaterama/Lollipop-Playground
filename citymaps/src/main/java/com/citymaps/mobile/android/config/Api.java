package com.citymaps.mobile.android.config;

import com.citymaps.mobile.android.model.vo.ApiBuild;

public abstract class Api {

	public static Api newInstance(ApiBuild build) {
		int version = build.getVersion();
		if (version >= 3) {
			return new ApiVersion3();
		} else {
			return new ApiBase();
		}
	}

	public abstract String getEndpointString(Endpoint endpoint, Object... args);

	public static enum Endpoint {
		BUILD,
		USER,
		PLACE,
		MAPS,
		MAPS_FOR_USER
	}
}
