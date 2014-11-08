package com.citymaps.mobile.android.model.vo;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import com.android.volley.Response;
import com.citymaps.mobile.android.app.SessionManager;
import com.citymaps.mobile.android.config.Endpoint;
import com.citymaps.mobile.android.model.GetGsonRequest;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * A class containing application configuration information as retrieved from the Internet.
 */
public final class Config implements Parcelable {

	public static final Creator<Config> CREATOR = new Creator<Config>() {
		@Override
		public Config createFromParcel(Parcel in) {
			return new Config(in);
		}

		@Override
		public Config[] newArray(int size) {
			return new Config[size];
		}
	};

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

	private Config(Parcel in) {
		mAppVersionCode = in.readInt();
		mMinVersionCode = in.readInt();
		mAppVersion = in.readString();
		mMinVersion = in.readString();
		mTimestamp = in.readLong();
		mUpgradePrompt = in.readString();
	}

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

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel out, int flags) {
		out.writeInt(mAppVersionCode);
		out.writeInt(mMinVersionCode);
		out.writeString(mAppVersion);
		out.writeString(mMinVersion);
		out.writeLong(mTimestamp);
		out.writeString(mUpgradePrompt);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	public static class GetRequest extends GetGsonRequest<Config> {

		public GetRequest(Context context, Response.Listener<Config> listener, Response.ErrorListener errorListener) {
			super(SessionManager.getInstance(context).getEnvironment().buildUrlString(Endpoint.Type.CONFIG),
					Config.class, null, listener, errorListener);
		}
	}
}
