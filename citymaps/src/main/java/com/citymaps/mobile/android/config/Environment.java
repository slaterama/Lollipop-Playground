package com.citymaps.mobile.android.config;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.util.Base64;
import com.citymaps.mobile.android.BuildConfig;
import com.citymaps.mobile.android.model.vo.User;
import com.citymaps.mobile.android.util.LogEx;
import com.citymaps.mobile.android.util.PackageUtils;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public abstract class Environment extends EndpointManager {

	private static final String BUILD_VARIANT_RELEASE = "release";

	protected static String encodeSecret(String key) {
		byte[] bytes = DigestUtils.sha1(key);
		String hex = new String(Hex.encodeHex(bytes));
		Hex.encodeHex(bytes);
		return Base64.encodeToString(hex.getBytes(), Base64.URL_SAFE | Base64.NO_WRAP | Base64.NO_PADDING);
	}

	public static Environment newInstance(Context context, Type type) {
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

	private Api mApi;

	protected Environment(Context context) {
		super();
		mContext = context.getApplicationContext();
		mServerMap = new HashMap<Server.Type, Server>(Server.Type.values().length);
		addServer(new Server(Server.Type.MAP_TILE, "tilecache.citymaps.com", Server.Protocol.SECURE));
		addServer(new Server(Server.Type.BUSINESS_TILE, "tilecache.citymaps.com", Server.Protocol.SECURE));
		addServer(new Server(Server.Type.REGION_TILE, "tilecache.citymaps.com", Server.Protocol.SECURE));
		addEndpoint(new Endpoint(Endpoint.Type.STATUS, Server.Type.API, "v2/status/version", 0));
	}

	@Override
	protected Endpoint getEndpoint(Endpoint.Type type) {
		Endpoint endpoint = super.getEndpoint(type);
		if (endpoint == null && mApi != null) {
			endpoint = mApi.getEndpoint(type);
		}
		return endpoint;
	}

	protected void addServer(Server server) {
		if (server != null) {
			mServerMap.put(server.getType(), server);
		}
	}

	protected Server getServer(Server.Type type) {
		return mServerMap.get(type);
	}

	public String buildUrlString(Endpoint.Type endpointType, User user, Object... args) {
		Endpoint endpoint = getEndpoint(endpointType);
		if (endpoint == null) {
			throw new IllegalStateException(String.format("No endpoint defined for type '%s'", endpointType));
		}

		Server.Type serverType = endpoint.getServerType();
		Server server = getServer(serverType);
		if (server == null) {
			throw new IllegalStateException(String.format("No server defined for type '%s'", serverType));
		}

		String file = endpoint.getFile();
		String formattedFile = String.format(file, args);

		String urlString;
		try {
			URL url = new URL(server.getProtocol().getValue(), server.getHost(), server.getPort(), formattedFile);
			urlString = url.toString();
		} catch (MalformedURLException e) {
			if (LogEx.isLoggable(LogEx.ERROR)) {
				LogEx.e(e.getMessage(), e);
			}
			urlString = "";
		}
		Uri.Builder builder = Uri.parse(urlString).buildUpon();

		int flags = endpoint.getFlags();
		if ((flags & APPEND_TIMESTAMP) == APPEND_TIMESTAMP) {
			builder.appendQueryParameter("timestamp", String.valueOf(System.currentTimeMillis()));
		}
		if ((flags & APPEND_ANDROID_VERSION) == APPEND_ANDROID_VERSION) {
			builder.appendQueryParameter("android_version", Build.VERSION.RELEASE);
		}
		if ((flags & APPEND_DEVICE_ID) == APPEND_DEVICE_ID) {
			builder.appendQueryParameter("device_id", Build.SERIAL);
		}

		if (user != null) {
			if ((flags & APPEND_USER_ID) == APPEND_USER_ID) {
				builder.appendQueryParameter("user_id", user.getId());
			}
			if ((flags & APPEND_CITYMAPS_TOKEN) == APPEND_CITYMAPS_TOKEN) {
				String citymapsToken = user.getCitymapsToken();
				if (citymapsToken != null) {
					builder.appendQueryParameter("citymaps_token", citymapsToken);
				}
			}
		}

		if ((flags & APPEND_ENDPOINT_VERSION) == APPEND_ENDPOINT_VERSION) {
			builder.appendQueryParameter("ev", mApi.getApiBuild());
		}

		if ((flags & APPEND_SECRET) == APPEND_SECRET) {
			String secret = PackageUtils.getCitymapsSecret(mContext);
			if (secret != null) {
				builder.appendQueryParameter("secret", encodeSecret(String.format("%s%s", builder.toString(), secret)));
			}
		}

		return builder.toString();
	}

	public String buildUrlString(Endpoint.Type endpointType) {
		return buildUrlString(endpointType, null);
	}

	public Context getContext() {
		return mContext;
	}

	public Api getApi() {
		return mApi;
	}

	public abstract String getGhostUserId();

	public Api registerVersion(int version, String build) {
		if (mApi != null) {
			int currentApiVersion = mApi.getApiVersion();
			String currentApiBuild = mApi.getApiBuild();
			if (currentApiVersion != version
					|| !TextUtils.equals(currentApiBuild, build)) {
				mApi = null;
			}
		}
		if (mApi == null) {
			mApi = Api.newInstance(version, build);
		}
		return mApi;
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
