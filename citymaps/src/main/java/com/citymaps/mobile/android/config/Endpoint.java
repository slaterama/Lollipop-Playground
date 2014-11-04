package com.citymaps.mobile.android.config;

public enum Endpoint {
	CONFIG(Host.CONFIG),
	BUILD(),
	USER(),
	PLACE(),
	COLLECTIONS(),
	COLLECTIONS_FOR_USER();

	private Host mHost;

	private Endpoint(Host host) {
		mHost = host;
	}

	private Endpoint() {
		this(Host.API);
	}

	public Host getHost() {
		return mHost;
	}
}
