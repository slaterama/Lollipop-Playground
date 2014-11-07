package com.citymaps.mobile.android.model.vo;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;

/**
 * A class representing information about the Citymaps API version.
 */
public class Version implements Parcelable {

	public static final Creator<Version> CREATOR = new Creator<Version>() {
		@Override
		public Version createFromParcel(Parcel in) {
			return new Version(in);
		}

		@Override
		public Version[] newArray(int size) {
			return new Version[size];
		}
	};

	/**
	 * The return code associated with this API request.
	 */
	@SerializedName("code")
	private int mCode;

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
	 * The time it took this request to run.
	 */
	@SerializedName("time")
	private int mTime;

	protected Version() {

	}

	private Version(Parcel in) {
		mCode = in.readInt();
		mVersion = in.readInt();
		mBuild = in.readString();
		mTime = in.readInt();
	}

	/**
	 * Returns the return code associated with this API request.
	 */
	public int getCode() {
		return mCode;
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

	/**
	 * Returns the time it took this request to run.
	 */
	public int getTime() {
		return mTime;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel out, int flags) {
		out.writeInt(mCode);
		out.writeInt(mVersion);
		out.writeString(mBuild);
		out.writeInt(mTime);
	}
}
