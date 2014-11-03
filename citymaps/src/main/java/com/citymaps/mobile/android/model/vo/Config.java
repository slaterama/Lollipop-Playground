package com.citymaps.mobile.android.model.vo;

import com.google.gson.annotations.SerializedName;

/**
 * A class containing application configuration information as retrieved from the Internet.
 */
public final class Config {

	/**
	 * The current app version. This value will be used to launch a "soft upgrade" message.
	 */
	@SerializedName("app_version_code")
	private int mAppVersionCode;

	/**
	 * The minimum app version. This value will be used to launch a "hard upgrade" message.
	 */
	@SerializedName("min_version_code")
	private int mMinVersionCode;

	/**
	 * A string representing the current app version. This is for display purposes only.
	 */
	@SerializedName("app_version")
	private String mAppVersion;

	/**
	 * A string representing the minimum app version. This is for display purposes only.
	 */
	@SerializedName("min_version")
	private String mMinVersion;

	/**
	 * A long representing the time at which the config json file was last updated.
	 */
	@SerializedName("timestamp")
	private long mTimestamp;

	/**
	 * The message to display when a hard upgrade is being enforced.
	 */
	@SerializedName("upgradePrompt")
	private String mUpgradePrompt;

	/**
	 * @return The current app version.
	 */
	public int getAppVersionCode() {
		return mAppVersionCode;
	}

	/**
	 * @return The minimum app version.
	 */
	public int getMinVersionCode() {
		return mMinVersionCode;
	}

	/**
	 * @return The String representation of the current app version.
	 */
	public String getAppVersion() {
		return mAppVersion;
	}

	/**
	 * @return The string representation of the minimum app version.
	 */
	public String getMinVersion() {
		return mMinVersion;
	}

	/**
	 * @return A long representing the time at which the app config file was last updated.
	 */
	public long getTimestamp() {
		return mTimestamp;
	}

	/**
	 * @return The message to display when a hard upgrade is being enforced.
	 */
	public String getUpgradePrompt() {
		return mUpgradePrompt;
	}
}
