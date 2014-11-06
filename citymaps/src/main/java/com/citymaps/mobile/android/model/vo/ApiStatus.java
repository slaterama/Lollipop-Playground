package com.citymaps.mobile.android.model.vo;

import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Value object class representing a Citymaps Api status.
 */
public class ApiStatus {

	/**
	 * The Citymaps Api version.
	 */
	@SerializedName("version")
	private int mVersion;

	/**
	 * The Citymaps Api build.
	 */
	@SerializedName("build")
	private String mBuild;

	public ApiStatus(int version, String build) {
		mVersion = version;
		mBuild = build;
	}

	/**
	 * Returns the Citymaps Api version.
	 * @return The Citymaps Api version.
	 */
	public int getVersion() {
		return mVersion;
	}

	/**
	 * Returns the Citymaps Api build.
	 * @return The Citymaps Api build.
	 */
	public String getBuild() {
		return mBuild;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this)
				.append("mVersion", mVersion)
				.append("mBuild", mBuild)
				.toString();
	}
}
