package com.citymaps.mobile.android.config;

import android.content.Context;
import com.citymaps.mobile.android.BuildConfig;

import java.util.HashMap;
import java.util.Map;

public abstract class Environment {

	private static final String BUILD_VARIANT_RELEASE = "release";
	private static final String BUILD_VARIANT_DEBUG = "debug";

	protected static final String GHOST_USER_ID_PROD = "345a8d0a-922b-4ff7-81ba-3000c2d55e4d";
	protected static final String GHOST_USER_ID_DEV = "28e20039-9742-45b6-a565-07a7acd88908";

	protected static final String CONFIG_ENDPOINT_PROD = "riak/appconfig/android_config.json";
	protected static final String CONFIG_ENDPOINT_DEV = "riak/appconfig/android_config_dev.json";

	public static Environment newInstance(Context context, Type type) {
		if (context == null) {
			throw new IllegalArgumentException("context can not be null");
		}

		if (type == null) {
			throw new IllegalArgumentException("type can not be null");
		}

		switch (type) {
			case DEVELOPMENT:
				return new EnvironmentDevelopment(context);
			case PRODUCTION:
			default:
				return new EnvironmentProduction(context);
		}
	}

	public static Environment newInstance(Context context) {
		return newInstance(context, Type.defaultType());
	}

	private Context mContext;

	private Map<Server.Type, Server> mServerMap;

	private Map<Endpoint.Type, Endpoint> mEndpointMap;

	protected Environment(Context context) {
		super();
		mContext = context.getApplicationContext();
		mServerMap = new HashMap<Server.Type, Server>(Server.Type.values().length);
		mEndpointMap = new HashMap<Endpoint.Type, Endpoint>();
	}

	public abstract Type getType();

	public Context getContext() {
		return mContext;
	}

	public abstract String getConfigEndpoint();

	protected Server createServer(Server.Type type) {
		switch (type) {
			case MAP_TILE:
			case BUSINESS_TILE:
			case REGION_TILE:
				return new Server(type, "tilecache.citymaps.com", Server.Protocol.SECURE);
			default:
				return null;
		}
	}

	public abstract String getGhostUserId();

	public Server getServer(Server.Type type) {
		Server server = mServerMap.get(type);
		if (server == null) {
			server = createServer(type);
			if (server == null) {
				throw new IllegalStateException(String.format("No server defined for '%s' in %s environment", type, getType().name().toLowerCase()));
			}
			mServerMap.put(type, server);
		}
		return server;
	}

	public static enum Type {
		PRODUCTION,
		DEVELOPMENT;

		public static Type defaultType() {
			String buildType = BuildConfig.BUILD_TYPE;
			//String flavor = BuildConfig.FLAVOR; <-- Unused for now
			if (BUILD_VARIANT_RELEASE.equals(buildType)) {
				return PRODUCTION;
			} else {
				return DEVELOPMENT;
			}
		}
	}
}
