package com.citymaps.mobile.android.confignew;

import com.citymaps.mobile.android.config.Host;

import java.util.HashMap;
import java.util.Map;

public abstract class Environment {

	public final static String STANDARD_PROTOCOL = "http";
	public final static String SECURE_PROTOCOL = "https";

	public static Environment newInstance(Type type) {
		switch (type) {
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

	public Environment() {
		super();
		mHostMap = new HashMap<Host, HostInfo>(Host.values().length);
		setHost(Host.ASSETS, STANDARD_PROTOCOL, "r.citymaps.com");
		setHost(Host.MAP_TILE, SECURE_PROTOCOL, "tilecache.citymaps.com");
		setHost(Host.BUSINESS_TILE, SECURE_PROTOCOL, "tilecache.citymaps.com");
		setHost(Host.REGION_TILE, SECURE_PROTOCOL, "tilecache.citymaps.com");
	}

	public abstract Type getType();

	protected void setHost(Host host, String protocol, String hostString, int port) {
		mHostMap.put(host, new HostInfo(host, protocol, hostString, port));
	}

	protected void setHost(Host host, String protocol, String hostString) {
		setHost(host, protocol, hostString, -1);
	}

	public static class HostInfo {
		private Host mHost;
		private String mProtocol;
		private String mHostString;
		private int mPort;

		public HostInfo(Host host) {
			super();
			mHost = host;
		}

		public HostInfo(Host host, String protocol, String hostString, int port) {
			this(host);
			mProtocol = protocol;
			mHostString = hostString;
			mPort = port;
		}

		public Host getHost() {
			return mHost;
		}

		public void setProtocol(String protocol) {
			mProtocol = protocol;
		}

		public String getProtocol() {
			return mProtocol;
		}

		public void setHostString(String hostString) {
			mHostString = hostString;
		}

		public String getHostString() {
			return mHostString;
		}

		public void setPort(int port) {
			mPort = port;
		}

		public int getPort() {
			return mPort;
		}
	}

	public static enum Type {
		PRODUCTION,
		DEVELOPMENT,
		STAGING
	}
}
