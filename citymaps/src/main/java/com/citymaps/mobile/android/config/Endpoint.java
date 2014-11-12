package com.citymaps.mobile.android.config;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class Endpoint {

	public static final int APPEND_NONE = 0x0000;
	public static final int APPEND_TIMESTAMP = 0x0001;
	public static final int APPEND_ANDROID_VERSION = 0x0002;
	public static final int APPEND_DEVICE_ID = 0x0008;
	public static final int APPEND_SECRET = 0x0010;

	public static final int APPEND_USER_ID = 0x0004;
	public static final int APPEND_CITYMAPS_TOKEN = 0x0020;

	public static final int APPEND_ENDPOINT_VERSION = 0x0040;

	public static final int APPEND_DEFAULT = APPEND_TIMESTAMP|APPEND_ANDROID_VERSION|APPEND_DEVICE_ID|APPEND_SECRET;
	public static final int APPEND_USER = APPEND_USER_ID|APPEND_CITYMAPS_TOKEN;
	public static final int APPEND_ALL = APPEND_DEFAULT|APPEND_USER;

	private Type mType;

	private Server.Type mServerType;

	private String mFile;

	private int mFlags;

	public Endpoint(Type type, Server.Type serverType, String file, int flags) {
		if (type == null) {
			throw new IllegalArgumentException("type can not be null");
		}
		if (serverType == null) {
			throw new IllegalArgumentException("serverType can not be null");
		}
		if (file == null) {
			throw new IllegalArgumentException("file can not be null");
		}
		mType = type;
		mServerType = serverType;
		mFile = file;
		mFlags = Math.max(flags, 0);
	}

	public Endpoint(Type type, String file, int flags) {
		this(type, Server.Type.API, file, flags);
	}

	public Endpoint(Type type, Server.Type serverType, String file) {
		this(type, serverType, file, APPEND_ALL);
	}

	public Endpoint(Type type, String file) {
		this(type, Server.Type.API, file, APPEND_ALL);
	}

	public Type getType() {
		return mType;
	}

	public Server.Type getServerType() {
		return mServerType;
	}

	public String getFile() {
		return mFile;
	}

	public int getFlags() {
		return mFlags;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this)
				.append("mType", mType)
				.append("mFile", mFile)
				.append("mFlags", mFlags)
				.toString();
	}

	public static enum Type {
		TERMS_OF_SERVICE,
		PRIVACY_POLICY,
		CONFIG,
		VERSION,
		COLLECTIONS,
		COLLECTIONS_FOR_USER,
		PLACE,
		USER,
		USER_LOGIN,
		USER_LOGIN_WITH_TOKEN,
		USER_REGISTER,
		USER_RESET_PASSWORD
	}
}
