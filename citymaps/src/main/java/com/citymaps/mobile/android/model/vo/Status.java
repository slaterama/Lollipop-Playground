package com.citymaps.mobile.android.model.vo;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;

/**
 * A class representing information about the Citymaps API version.
 */
public class Status implements Parcelable {

	public static final Creator<Status> CREATOR = new Creator<Status>() {
		@Override
		public Status createFromParcel(Parcel in) {
			return new Status(in);
		}

		@Override
		public Status[] newArray(int size) {
			return new Status[size];
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

	protected Status() {

	}

	private Status(Parcel in) {
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
