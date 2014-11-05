package com.citymaps.mobile.android.confignew;

import com.citymaps.mobile.android.config.Host;

public class EnvironmentStaging extends Environment {

	public EnvironmentStaging() {
		super();
		setHost(Host.CONFIG, STANDARD_PROTOCOL, "riak.citymaps.com", 8098);
		setHost(Host.API, STANDARD_PROTOCOL, "s-coreapi.citymaps.com");
		setHost(Host.SEARCH, STANDARD_PROTOCOL, "s-coresearch.citymaps.com");
		setHost(Host.MOBILE, STANDARD_PROTOCOL, "s-m.citymaps.com");
	}

	@Override
	public Type getType() {
		return Type.STAGING;
	}
}
