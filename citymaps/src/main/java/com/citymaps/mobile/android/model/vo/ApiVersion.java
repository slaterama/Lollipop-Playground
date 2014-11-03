package com.citymaps.mobile.android.model.vo;

import com.google.gson.annotations.SerializedName;

/**
 * A class representing information about the Citymaps API version.
 */
public class ApiVersion {

	/**
	 * The return code associated with this API request.
	 */
	@SerializedName("code")
	private int mCode;

	/**
	 * The error string associated with this API request if an error occurred.
	 */
	@SerializedName("error")
	private String mError;

	/**
	 * The Citymaps API version number.
	 */
	@SerializedName("version")
	private int mVersion;

	/**
	 * The Citymaps API build string.
	 */
	@SerializedName("build")
	private String mBuild;

	/**
	 * Returns the return code associated with this API request.
	 */
	public int getCode() {
		return mCode;
	}

	/**
	 * Returns the error string associated with this API request if an error occurred.
	 */
	public String getError() {
		return mError;
	}

	/**
	 * Returns the Citymaps API version number.
	 */
	public int getVersion() {
		return mVersion;
	}

	/**
	 * Returns the Citymaps API build string.
	 */
	public String getBuild() {
		return mBuild;
	}
}
