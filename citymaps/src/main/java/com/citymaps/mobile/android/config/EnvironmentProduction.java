package com.citymaps.mobile.android.config;

import android.content.Context;

public class EnvironmentProduction extends Environment {

	protected EnvironmentProduction(Context context) {
		super(context);
	}

	@Override
	public Type getType() {
		return Type.PRODUCTION;
	}

	@Override
	public String getConfigEndpoint() {
		return CONFIG_ENDPOINT_PROD;
	}

	@Override
	protected Server createServer(Server.Type type) {
		switch (type) {
			case API:
				return new Server(type, "coreapi.citymaps.com", Server.Protocol.SECURE);
			case SEARCH:
				return new Server(type, "coresearch.citymaps.com", Server.Protocol.SECURE);
			case MOBILE:
				return new Server(type, "m.citymaps.com", Server.Protocol.SECURE);
			case ASSETS:
				return new Server(type, "r.citymaps.com", Server.Protocol.STANDARD);
			default:
				return super.createServer(type);
		}
	}

	@Override
	public String getGhostUserId() {
		return GHOST_USER_ID_PROD;
	}
}
