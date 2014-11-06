package com.citymaps.mobile.android.confignew;

import android.content.Context;
import com.citymaps.mobile.android.BuildConfig;
import com.citymaps.mobile.android.config.Server;

public class EnvironmentProduction extends Environment {

	protected EnvironmentProduction(Context context) {
		super(context);
	}

	@Override
	public String getGhostUserId() {
		return BuildConfig.GHOST_USER_ID_PROD;
	}

	@Override
	protected void onCreate() {
		super.onCreate();
		addServer(new Server(Server.Type.API, "coreapi.citymaps.com", Server.Protocol.SECURE));
		addServer(new Server(Server.Type.SEARCH, "coresearch.citymaps.com", Server.Protocol.SECURE));
		addServer(new Server(Server.Type.MOBILE, "m.citymaps.com", Server.Protocol.SECURE));
		addServer(new Server(Server.Type.ASSETS, "r.citymaps.com", Server.Protocol.STANDARD));
	}
}
