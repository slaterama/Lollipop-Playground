package com.citymaps.mobile.android.config;

import android.content.Context;
import com.citymaps.mobile.android.BuildConfig;

public class EnvironmentDev extends Environment {

	private final static int API_VERSION = 2;
	private final static String API_BUILD = "3.0.0";

	protected EnvironmentDev(Context context) {
		super(context);
		addServer(new Server(Server.Type.API, "ndev-coreapi.citymaps.com", Server.Protocol.STANDARD));
		addServer(new Server(Server.Type.SEARCH, "ndev-coresearch.citymaps.com", Server.Protocol.STANDARD));
		addServer(new Server(Server.Type.MOBILE, "ndev-coreweb.citymaps.com", Server.Protocol.STANDARD));
		addServer(new Server(Server.Type.ASSETS, "riak.citymaps.com", Server.Protocol.STANDARD, 8098));

	}

	@Override
	public String getGhostUserId() {
		return BuildConfig.GHOST_USER_ID_DEV;
	}

	@Override
	public Type getType() {
		return Type.DEVELOPMENT;
	}

	@Override
	protected Api onCreateApi() {
		return Api.newInstance(this, API_VERSION, API_BUILD);
	}
}
