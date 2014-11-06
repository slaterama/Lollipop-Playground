package com.citymaps.mobile.android.confignew;

import android.content.Context;
import com.citymaps.mobile.android.BuildConfig;
import com.citymaps.mobile.android.config.Endpoint;
import com.citymaps.mobile.android.config.Server;

public class EnvironmentDevelopment extends Environment {

	protected EnvironmentDevelopment(Context context) {
		super(context);
	}

	@Override
	public String getGhostUserId() {
		return BuildConfig.GHOST_USER_ID_DEV;
	}

	@Override
	protected void onCreate() {
		super.onCreate();
		addServer(new Server(Server.Type.API, "dev-coreapi.citymaps.com", Server.Protocol.STANDARD));
		addServer(new Server(Server.Type.SEARCH, "dev-coresearch.citymaps.com", Server.Protocol.STANDARD));
		addServer(new Server(Server.Type.MOBILE, "dev-m.citymaps.com", Server.Protocol.STANDARD));
		addServer(new Server(Server.Type.ASSETS, "riak.citymaps.com", Server.Protocol.STANDARD, 8098));
		addEndpoint(new Endpoint(Endpoint.Type.CONFIG, Server.Type.ASSETS, "riak/appconfig/android_config_dev.json", 0));
	}
}
