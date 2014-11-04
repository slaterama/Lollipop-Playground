package com.citymaps.mobile.android.model.vo;

import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Value object class representing a Citymaps API build.
 */
public class ApiBuild {

	/**
	 * The Citymaps API version.
	 */
	@SerializedName("version")
	private int mVersionNumber;

	/**
	 * The Citymaps API build string.
	 */
	@SerializedName("build")
	private String mBuildString;

	public ApiBuild(int versionNumber, String buildString) {
		mVersionNumber = versionNumber;
		mBuildString = buildString;
	}

	/**
	 * Returns the Citymaps API version.
	 * @return The Citymaps API version.
	 */
	public int getVersionNumber() {
		return mVersionNumber;
	}

	/**
	 * Returns the Citymaps API build string.
	 * @return The build string.
	 */
	public String getBuildString() {
		return mBuildString;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
