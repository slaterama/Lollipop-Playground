package com.citymaps.mobile.android.config;

public class EnvironmentProduction extends Environment {

	protected EnvironmentProduction() {
		super();
		configureHost(Host.CONFIG, STANDARD_PROTOCOL, "r.citymaps.com");
		configureHost(Host.API, SECURE_PROTOCOL, "coreapi.citymaps.com");
		configureHost(Host.SEARCH, SECURE_PROTOCOL, "coresearch.citymaps.com");
		configureHost(Host.MOBILE, SECURE_PROTOCOL, "m.citymaps.com");
	}

	@Override
	public Type getType() {
		return Type.PRODUCTION;
	}
}
