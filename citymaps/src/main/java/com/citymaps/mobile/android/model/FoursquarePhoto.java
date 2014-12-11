package com.citymaps.mobile.android.model;

import com.google.gson.annotations.SerializedName;

public class FoursquarePhoto {

	public static final String ID = "id";
	public static final String CREATED_AT = "createdAt";
	public static final String SOURCE = "source";
	public static final String PREFIX = "prefix";
	public static final String SUFFIX = "suffix";
	public static final String WIDTH = "width";
	public static final String HEIGHT = "height";
	public static final String USER = "user";
	public static final String VISIBILITY = "visibility";

	@SerializedName(ID)
	private String mId;

	@SerializedName(CREATED_AT)
	private long mCreatedAt;

	@SerializedName(SOURCE)
	private Source mSource;

	@SerializedName(PREFIX)
	private String mPrefix;

	@SerializedName(SUFFIX)
	private String mSuffix;

	@SerializedName(WIDTH)
	private int mWidth;

	@SerializedName(HEIGHT)
	private int mHeight;

	@SerializedName(USER)
	private User mUser;

	@SerializedName(VISIBILITY)
	private String mVisibility;

	public String getId() {
		return mId;
	}

	public long getCreatedAt() {
		return mCreatedAt;
	}

	public Source getSource() {
		return mSource;
	}

	public String getPhotoUrl(int width, int height) {
		return String.format("%s%dx%d%s", mPrefix, width, height, mSuffix);
	}

	public String getPhotoUrl() {
		return getPhotoUrl(mWidth, mHeight);
	}

	public String getPrefix() {
		return mPrefix;
	}

	public String getSuffix() {
		return mSuffix;
	}

	public int getWidth() {
		return mWidth;
	}

	public int getHeight() {
		return mHeight;
	}

	public User getUser() {
		return mUser;
	}

	public String getVisibility() {
		return mVisibility;
	}

	public static class Source {
		public static final String NAME = "name";
		public static final String URL = "url";

		@SerializedName(NAME)
		private String mName;

		@SerializedName(URL)
		private String mUrl;

		public String getName() {
			return mName;
		}

		public String getUrl() {
			return mUrl;
		}
	}

	public static class User {
		public static final String ID = "id";
		public static final String FIRST_NAME = "firstName";
		public static final String LAST_NAME = "lastName";
		public static final String GENDER = "gender";
		public static final String PHOTO = "photo";

		@SerializedName(ID)
		private String mId;

		@SerializedName(FIRST_NAME)
		private String mFirstName;

		@SerializedName(LAST_NAME)
		private String mLastName;

		@SerializedName(GENDER)
		private String mGender;

		@SerializedName(PHOTO)
		private Photo mPhoto;

		public static class Photo {
			public static final String PREFIX = "prefix";
			public static final String SUFFIX = "suffix";

			@SerializedName(PREFIX)
			private String mPrefix;

			@SerializedName(SUFFIX)
			private String mSuffix;

			public String getPrefix() {
				return mPrefix;
			}

			public String getSuffix() {
				return mSuffix;
			}
		}
	}

}
