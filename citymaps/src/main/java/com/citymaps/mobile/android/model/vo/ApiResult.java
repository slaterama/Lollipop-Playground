package com.citymaps.mobile.android.model.vo;

import com.google.gson.annotations.SerializedName;

/**
 * A class representing a response from the Citymaps API server.
 */
public class ApiResult {

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
	 * A class representing metadata associated with a Citymaps result.
	 */
	public static class Meta {

		// TODO Version 2 of CitymapsResult *HAS* No "Meta" object.

		/**
		 * The version string.
		 */
		@SerializedName("version")
		private String mVersion;

		/**
		 * The API version string.
		 */
		@SerializedName("api_version")
		private int mApiVersion;

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

		/**
		 * Returns the version string.
		 */
		public String getVersion() {
			return mVersion;
		}

		/**
		 * Returns the API version string.
		 */
		public int getApiVersion() {
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

		/**
		 * A class representing an entity making an API call.
		 */
		public static class Caller {

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
		}
	}
}
