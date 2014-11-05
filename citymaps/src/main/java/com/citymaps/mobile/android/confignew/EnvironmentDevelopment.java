package com.citymaps.mobile.android.confignew;

import com.citymaps.mobile.android.config.Host;

public class EnvironmentDevelopment extends Environment {

	public EnvironmentDevelopment() {
		super();
		setHost(Host.CONFIG, STANDARD_PROTOCOL, "riak.citymaps.com", 8098);
		setHost(Host.API, STANDARD_PROTOCOL, "dev-coreapi.citymaps.com");
		setHost(Host.SEARCH, STANDARD_PROTOCOL, "dev-coresearch.citymaps.com");
		setHost(Host.MOBILE, STANDARD_PROTOCOL, "dev-m.citymaps.com");
	}

	@Override
	public Type getType() {
		return Type.DEVELOPMENT;
	}
}
