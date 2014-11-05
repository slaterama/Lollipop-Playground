package com.citymaps.mobile.android.confignew;

import com.citymaps.mobile.android.config.Host;

public class EnvironmentProduction extends Environment {

	public EnvironmentProduction() {
		super();
		setHost(Host.CONFIG, STANDARD_PROTOCOL, "r.citymaps.com");
		setHost(Host.API, SECURE_PROTOCOL, "coreapi.citymaps.com");
		setHost(Host.SEARCH, SECURE_PROTOCOL, "coresearch.citymaps.com");
		setHost(Host.MOBILE, SECURE_PROTOCOL, "m.citymaps.com");
	}

	@Override
	public Type getType() {
		return Type.PRODUCTION;
	}
}
