package com.citymaps.mobile.android.model;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;

public abstract class ResultWrapperV2<T> implements ResultWrapper<T>, Parcelable {

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
	 * The Citymaps API error message (if an error occurred).
	 */
	@SerializedName("message")
	private String mMessage;

	/**
	 * The Citymaps API error reason (if an error occurred).
	 */
	@SerializedName("reason")
	private String mReason;

	/**
	 * The time it took this request to run.
	 */
	@SerializedName("time")
	private long mTime;

	public ResultWrapperV2() {
	}

	protected ResultWrapperV2(Parcel in) {
		mCode = in.readInt();
		mVersion = in.readInt();
		mBuild = in.readString();
		mMessage = in.readString();
		mReason = in.readString();
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
	 * Returns the Citymaps API error message.
	 */
	public String getMessage() {
		return mMessage;
	}

	/**
	 * Returns the Citymaps API error reason.
	 */
	public String getReason() {
		return mReason;
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
		out.writeString(mMessage);
		out.writeString(mReason);
		out.writeLong(mTime);
	}
}
