package com.citymaps.mobile.android.model;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;

public abstract class ResultWrapperV1 implements ResultWrapper, Parcelable {

	/**
	 * The return code associated with this result.
	 */
	@SerializedName("code")
	private int mCode;

	/**
	 * The error string associated with this result if an error occurred.
	 */
	@SerializedName("error")
	private String mError;

	/**
	 * The number of elements returned.
	 */
	@SerializedName("count")
	private int mCount;

	/**
	 * Any metadata associated with this result.
	 */
	@SerializedName("meta")
	private Meta mMeta;

	/**
	 * The time it took this request to run.
	 */
	@SerializedName("time")
	private long mTime;

	private ResultWrapperV1(Parcel in) {
		mCode = in.readInt();
		mError = in.readString();
		mCount = in.readInt();
		mMeta = in.readParcelable(Meta.class.getClassLoader());
		mTime = in.readLong();
	}

	/**
	 * Returns the return code associated with this result.
	 */
	public int getCode() {
		return mCode;
	}

	/**
	 * Returns the error string associated with this result if an error occurred.
	 */
	public String getError() {
		return mError;
	}

	/**
	 * Returns the number of elements returned with this result.
	 */
	public int getCount() {
		return mCount;
	}

	/**
	 * Returns any metadata associated with this result.
	 */
	public Meta getMeta() {
		return mMeta;
	}

	/**
	 * Returns the time it took this request to run.
	 */
	@Override
	public long getTime() {
		return mTime;
	}

	@Override
	public int getVersion() {
		try {
			return Integer.parseInt(mMeta.getApiVersion());
		} catch (NumberFormatException e) {
			return 0;
		} catch (NullPointerException e) {
			return 0;
		}
	}

	@Override
	public String getBuild() {
		try {
			return mMeta.getVersion();
		} catch (NullPointerException e) {
			return null;
		}
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel out, int flags) {
		out.writeInt(mCode);
		out.writeString(mError);
		out.writeInt(mCount);
		out.writeParcelable(mMeta, flags);
		out.writeLong(mTime);
	}

	/**
	 * A class representing metadata associated with a Citymaps result.
	 */
	public static class Meta implements Parcelable {

		public static final Parcelable.Creator<Meta> CREATOR = new Parcelable.Creator<Meta>() {
			@Override
			public Meta createFromParcel(Parcel in) {
				return new Meta(in);
			}

			@Override
			public Meta[] newArray(int size) {
				return new Meta[size];
			}
		};

		/**
		 * The version string.
		 */
		@SerializedName("version")
		private String mVersion;

		/**
		 * The API version string.
		 */
		@SerializedName("api_version")
		private String mApiVersion;

		/**
		 * The amount of time elapsed to make this API call.
		 */
		@SerializedName("elapsed")
		private float mElapsed;

		/**
		 * The entity that made this API call.
		 */
		@SerializedName("caller")
		private Caller mCaller;

		private Meta(Parcel in) {
			mVersion = in.readString();
			mApiVersion = in.readString();
			mElapsed = in.readFloat();
			mCaller = in.readParcelable(Caller.class.getClassLoader());
		}

		/**
		 * Returns the version string.
		 */
		public String getVersion() {
			return mVersion;
		}

		/**
		 * Returns the API version string.
		 */
		public String getApiVersion() {
			return mApiVersion;
		}

		/**
		 * Returns the amount of time elapsed to make this API call.
		 */
		public float getElapsed() {
			return mElapsed;
		}

		/**
		 * Returns the entity that made this API call.
		 */
		public Caller getCaller() {
			return mCaller;
		}

		@Override
		public int describeContents() {
			return 0;
		}

		@Override
		public void writeToParcel(Parcel out, int flags) {
			out.writeString(mVersion);
			out.writeString(mApiVersion);
			out.writeFloat(mElapsed);
			out.writeParcelable(mCaller, flags);
		}

		/**
		 * A class representing an entity making an API call.
		 */
		public static class Caller implements Parcelable {

			public static final Parcelable.Creator<Caller> CREATOR = new Parcelable.Creator<Caller>() {
				@Override
				public Caller createFromParcel(Parcel in) {
					return new Caller(in);
				}

				@Override
				public Caller[] newArray(int size) {
					return new Caller[size];
				}
			};

			/**
			 * The user id of the caller.
			 */
			@SerializedName("user id")
			private String mUserId;

			/**
			 * The name of the caller.
			 */
			@SerializedName("name")
			private String mName;

			/**
			 * The admin level of the caller.
			 */
			@SerializedName("admin level")
			private String mAdminLevel;

			/**
			 * Whether the caller is public.
			 */
			@SerializedName("is public")
			private String mPublic;

			private Caller(Parcel in) {
				mUserId = in.readString();
				mName = in.readString();
				mAdminLevel = in.readString();
				mPublic = in.readString();
			}

			/**
			 * Returns the user id of the caller.
			 */
			public String getUserId() {
				return mUserId;
			}

			/**
			 * Returns the name of the caller.
			 */
			public String getName() {
				return mName;
			}

			/**
			 * Returns the admin level of the caller.
			 */
			public String getAdminLevel() {
				return mAdminLevel;
			}

			/**
			 * Returns whether the caller is public.
			 */
			public boolean isPublic() {
				return Boolean.parseBoolean(mPublic);
			}

			@Override
			public int describeContents() {
				return 0;
			}

			@Override
			public void writeToParcel(Parcel out, int flags) {
				out.writeString(mUserId);
				out.writeString(mName);
				out.writeString(mAdminLevel);
				out.writeString(mPublic);
			}
		}
	}
}
