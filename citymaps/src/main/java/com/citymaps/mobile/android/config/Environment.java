package com.citymaps.mobile.android.config;

import android.text.TextUtils;
import com.citymaps.mobile.android.os.BuildVersion;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public abstract class Environment {

	private final static String DEVELOPMENT_QUALIFIER_REGEX = "^dev";
	private final static String STAGING_QUALIFIER_REGEX = "^stag";

	public final static String STANDARD_PROTOCOL = "http";
	public final static String SECURE_PROTOCOL = "https";

	public static Environment newInstance(BuildVersion buildVersion) {
		return newInstance(Type.fromBuildVersion(buildVersion));
	}

	public static Environment newInstance(Type type) {
		if (type == null) {
			return null;
		} else switch (type) {
			case DEVELOPMENT:
				return new EnvironmentDevelopment();
			case STAGING:
				return new EnvironmentStaging();
			case PRODUCTION:
			default:
				return new EnvironmentProduction();
		}
	}

	private Map<Host, HostInfo> mHostMap;

	protected Environment() {
		super();
		mHostMap = new HashMap<Host, HostInfo>(Host.values().length);
		configureHost(Host.ASSETS, STANDARD_PROTOCOL, "r.citymaps.com");
		configureHost(Host.MAP_TILE, SECURE_PROTOCOL, "tilecache.citymaps.com");
		configureHost(Host.BUSINESS_TILE, SECURE_PROTOCOL, "tilecache.citymaps.com");
		configureHost(Host.REGION_TILE, SECURE_PROTOCOL, "tilecache.citymaps.com");
	}

	public abstract Type getType();

	protected void configureHost(Host host, String protocol, String hostString, int port) {
		HostInfo info = mHostMap.get(host);
		if (info == null) {
			info = new HostInfo();
		}
		info.mProtocol = protocol;
		info.mHost = host;
		info.mHostString = hostString;
		info.mPort = port;
		mHostMap.put(host, info);
	}

	protected void configureHost(Host host, String protocol, String hostString) {
		configureHost(host, protocol, hostString, -1);
	}

	public URL buildUrl(Host host, String endpointString, Object... args) throws MalformedURLException {
		HostInfo info = mHostMap.get(host);
		return new URL(info.mProtocol, info.mHostString, info.mPort, String.format(endpointString, args));
	}

	protected static class HostInfo {
		protected String mProtocol;
		protected Host mHost;
		protected String mHostString;
		protected int mPort;
	}

	public static enum Type {
		PRODUCTION,
		DEVELOPMENT,
		STAGING;

		public static Type fromBuildVersion(BuildVersion buildVersion) {
			Type type = null;
			if (buildVersion != null) {
				String qualifier = buildVersion.getQualifier();
				if (TextUtils.isEmpty(qualifier)) {
					type = PRODUCTION;
				} else if (qualifier.matches(DEVELOPMENT_QUALIFIER_REGEX)) {
					type = DEVELOPMENT;
				} else if (qualifier.matches(STAGING_QUALIFIER_REGEX)) {
					type = STAGING;
				} else {
					type = PRODUCTION;
				}
			}
			return type;
		}
	}
}
