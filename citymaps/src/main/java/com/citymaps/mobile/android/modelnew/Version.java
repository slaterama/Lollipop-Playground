package com.citymaps.mobile.android.modelnew;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;

public class Version implements Parcelable {

	public static final Parcelable.Creator<Version> CREATOR = new Parcelable.Creator<Version>() {
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
	private long mTime;

	public Version() {
	}

	protected Version(Parcel in) {
		mCode = in.readInt();
		mVersion = in.readInt();
		mBuild = in.readString();
		mTime = in.readLong();
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
	public long getTime() {
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
		out.writeLong(mTime);
	}
}
