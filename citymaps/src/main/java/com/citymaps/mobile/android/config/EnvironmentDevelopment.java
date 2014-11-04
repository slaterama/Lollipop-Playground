package com.citymaps.mobile.android.config;

public class EnvironmentDevelopment extends Environment {

	protected EnvironmentDevelopment() {
		super();
		configureHost(Host.CONFIG, STANDARD_PROTOCOL, "riak.citymaps.com", 8098);
		configureHost(Host.API, STANDARD_PROTOCOL, "dev-coreapi.citymaps.com");
		configureHost(Host.SEARCH, STANDARD_PROTOCOL, "dev-coresearch.citymaps.com");
		configureHost(Host.MOBILE, STANDARD_PROTOCOL, "dev-m.citymaps.com");
	}

	@Override
	public Type getType() {
		return Type.DEVELOPMENT;
	}
}
