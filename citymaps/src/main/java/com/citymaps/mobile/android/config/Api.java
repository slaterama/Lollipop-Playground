package com.citymaps.mobile.android.config;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.util.Base64;
import com.citymaps.mobile.android.model.vo.User;
import com.citymaps.mobile.android.os.SoftwareVersion;
import com.citymaps.mobile.android.util.PackageUtils;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.MissingFormatArgumentException;

import static com.citymaps.mobile.android.config.Endpoint.*;

public abstract class Api {

	protected static String encodeSecret(String key) {
		byte[] bytes = DigestUtils.sha1(key);
		String hex = new String(Hex.encodeHex(bytes));
		Hex.encodeHex(bytes);
		return Base64.encodeToString(hex.getBytes(), Base64.URL_SAFE | Base64.NO_WRAP).replace('=', '_');
	}

	public static Api newInstance(Environment environment, int apiVersion, SoftwareVersion apiBuild) {
		if (environment == null) {
			throw new IllegalArgumentException("environment can not be null");
		}

		if (apiVersion >= 3) {
			return new ApiVersion3(environment);
		} else {
			return new ApiBase(environment);
		}
	}

	public static Api newInstance(Environment environment, int apiVersion) {
		return newInstance(environment, apiVersion, null);
	}

	protected Environment mEnvironment;

	protected Context mContext;

	private Map<Endpoint.Type, Endpoint> mEndpointMap;

	protected Api(Environment environment) {
		mEnvironment = environment;
		mContext = environment.getContext();
		mEndpointMap = new HashMap<Endpoint.Type, Endpoint>(Endpoint.Type.values().length);
	}

	public abstract int getApiVersion();

	public abstract SoftwareVersion getApiBuild();

	protected abstract Endpoint createEndpoint(Endpoint.Type type);

	public Endpoint getEndpoint(Endpoint.Type type) {
		Endpoint endpoint = mEndpointMap.get(type);
		if (endpoint == null) {
			endpoint = createEndpoint(type);
			if (endpoint == null) {
				throw new IllegalStateException(String.format("No endpoint defined for '%s'", type));
			}
			mEndpointMap.put(type, endpoint);
		}
		return endpoint;
	}

	public String buildUrlString(Endpoint.Type type, Object... args) throws MalformedURLException {
		Endpoint endpoint = getEndpoint(type);
		Server server = mEnvironment.getServer(endpoint.getServerType());

		String file = endpoint.getFile();

		String formattedFile;
		if (args == null) {
			formattedFile = file;
		} else try {
			formattedFile = String.format(file, args);
		} catch (MissingFormatArgumentException e) {
			formattedFile = file;
		}

		URL url = new URL(server.getProtocol().getValue(), server.getHost(), server.getPort(), formattedFile);
		Uri.Builder builder = Uri.parse(url.toString()).buildUpon();

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

		User user = null; // TODO How should I get the current user?
		user = new User();
		user.setId("8ad760c4-3eb5-42e8-aa23-8259856e7763");
		user.setCitymapsToken("N0uCaPGjdHwuedfBvyvg8MrqXzmsHJ");

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
			builder.appendQueryParameter("ev", getApiBuild().toString());
		}

		if ((flags & APPEND_SECRET) == APPEND_SECRET) {
			String secret = PackageUtils.getCitymapsSecret(mContext);
			if (secret != null) {
				builder.appendQueryParameter("secret", encodeSecret(String.format("%s%s", builder.toString(), secret)));
			}
		}

		return builder.toString();
	}
}
