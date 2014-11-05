package com.citymaps.mobile.android.config;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class Server {

	public static final int STANDARD_PORT = -1;

	private Type mType;

	private String mHost;

	private Protocol mProtocol;

	private int mPort;

	public Server(Type type, String host, Protocol protocol, int port) {
		if (type == null) {
			throw new IllegalArgumentException("type can not be null");
		}
		if (host == null) {
			throw new IllegalArgumentException("host can not be null");
		}
		if (protocol == null) {
			throw new IllegalArgumentException("protocol can not be null");
		}
		mType = type;
		mHost = host;
		mProtocol = protocol;
		mPort = port;
	}

	public Server(Type type, String host, Protocol protocol) {
		this(type, host, protocol, STANDARD_PORT);
	}

	public Server(Type type, String host) {
		this(type, host, Protocol.SECURE, STANDARD_PORT);
	}

	public Type getType() {
		return mType;
	}

	public String getHost() {
		return mHost;
	}

	public Protocol getProtocol() {
		return mProtocol;
	}

	public int getPort() {
		return mPort;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this)
				.append("mType", mType)
				.append("mHost", mHost)
				.append("mProtocol", mProtocol)
				.append("mPort", mPort)
				.toString();
	}

	public static enum Type {
		API,
		SEARCH,
		MOBILE,
		ASSETS,
		MAP_TILE,
		BUSINESS_TILE,
		REGION_TILE
	}

	public static enum Protocol {
		STANDARD("http"),
		SECURE("https");

		private String mValue;

		private Protocol(String value) {
			mValue = value;
		}

		public String getValue() {
			return mValue;
		}
	}
}
