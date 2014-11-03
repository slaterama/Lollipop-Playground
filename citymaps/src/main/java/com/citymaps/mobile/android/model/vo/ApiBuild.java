package com.citymaps.mobile.android.model.vo;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Value object class representing a Citymaps API build.
 */
public class ApiBuild {

	/**
	 * The Citymaps API version.
	 */
	private int mVersion;

	/**
	 * The Citymaps API build string.
	 */
	private String mBuild;

	public ApiBuild(int version, String build) {
		mVersion = version;
		mBuild = build;
	}

	/**
	 * Returns the Citymaps API version.
	 * @return The Citymaps API version.
	 */
	public int getVersion() {
		return mVersion;
	}

	/**
	 * Returns the Citymaps API build string.
	 * @return The build string.
	 */
	public String getBuild() {
		return mBuild;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
