package com.citymaps.mobile.android.config;

public class EnvironmentStaging extends Environment {

	protected EnvironmentStaging() {
		super();
		configureHost(Host.CONFIG, STANDARD_PROTOCOL, "riak.citymaps.com", 8098);
		configureHost(Host.API, STANDARD_PROTOCOL, "s-coreapi.citymaps.com");
		configureHost(Host.SEARCH, STANDARD_PROTOCOL, "s-coresearch.citymaps.com");
		configureHost(Host.MOBILE, STANDARD_PROTOCOL, "s-m.citymaps.com");
	}

	@Override
	public Type getType() {
		return Type.STAGING;
	}
}
