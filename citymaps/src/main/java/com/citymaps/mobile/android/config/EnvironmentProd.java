package com.citymaps.mobile.android.config;

import android.content.Context;
import com.citymaps.mobile.android.BuildConfig;

public class EnvironmentProd extends Environment {

	protected EnvironmentProd(Context context) {
		super(context);
		addServer(new Server(Server.Type.API, "coreapi.citymaps.com", Server.Protocol.SECURE));
		addServer(new Server(Server.Type.SEARCH, "coresearch.citymaps.com", Server.Protocol.SECURE));
		addServer(new Server(Server.Type.MOBILE, "m.citymaps.com", Server.Protocol.SECURE));
		addServer(new Server(Server.Type.ASSETS, "r.citymaps.com", Server.Protocol.STANDARD));
	}

	@Override
	public String getGhostUserId() {
		return BuildConfig.GHOST_USER_ID_PROD;
	}

	@Override
	public Type getType() {
		return Type.PRODUCTION;
	}

	@Override
	protected Api onCreateApi() {
		return Api.newInstance(this, Api.Version.V2);
	}
}
