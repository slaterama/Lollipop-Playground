package com.citymaps.mobile.android.config;

import android.content.Context;

public class EnvironmentDevelopment extends Environment {

	protected EnvironmentDevelopment(Context context) {
		super(context);
	}

	@Override
	public Type getType() {
		return Type.DEVELOPMENT;
	}

	@Override
	public String getConfigEndpoint() {
		return CONFIG_ENDPOINT_DEV;
	}

	@Override
	protected Server createServer(Server.Type type) {
		switch (type) {
			case API:
				return new Server(type, "dev-coreapi.citymaps.com", Server.Protocol.STANDARD);
			case SEARCH:
				return new Server(type, "dev-coresearch.citymaps.com", Server.Protocol.STANDARD);
			case MOBILE:
				return new Server(type, "dev-m.citymaps.com", Server.Protocol.STANDARD);
			case ASSETS:
				return new Server(type, "riak.citymaps.com", Server.Protocol.STANDARD, 8098);
			default:
				return super.createServer(type);
		}
	}

	@Override
	public String getGhostUserId() {
		return GHOST_USER_ID_DEV;
	}
}
