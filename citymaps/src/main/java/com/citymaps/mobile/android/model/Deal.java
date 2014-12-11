package com.citymaps.mobile.android.model;

import com.google.gson.annotations.SerializedName;

import java.util.Observable;

public class Deal extends Observable {

	private static final String DEAL_ID = "deal_id";
	private static final String DESCRIPTION = "description";
	private static final String END_TIME = "end_time";
	private static final String FULFILLMENT_URL = "fulfillment_url";
	private static final String LABEL = "label";
	private static final String SOURCE = "source";
	private static final String START_TIME = "start_time";
	private static final String THUMBNAIL_IMAGE = "thumbnail_image";

	private static final String GROUPON_REGEX = "^http[s]?:\\/\\/img\\.grouponcdn\\.com\\/.*t\\d+x\\d+\\.jpg$";
	private static final String GROUPON_REMOVE_THUMBNAIL_REGEX = "\\/v1\\/t\\d+x\\d+\\.jpg$";
	private static final String GROUPON_REPLACE_THUMBNAIL_SIZE_REGEX = "t\\d+x\\d+";

	@SerializedName(DEAL_ID)
	private String mId;

	@SerializedName(DESCRIPTION)
	private String mDescription;

	@SerializedName(END_TIME)
	private long mEndTimeMillis;

	@SerializedName(FULFILLMENT_URL)
	private String mFulfillmentUrl;

	@SerializedName(LABEL)
	private String mLabel;

	@SerializedName(SOURCE)
	private String mSource;

	@SerializedName(START_TIME)
	private long mStartTimeMillis;

	@SerializedName(THUMBNAIL_IMAGE)
	private String mThumbnailImage;

	public String getId() {
		return mId;
	}

	public void setId(String id) {
		mId = id;
		setChanged();
		notifyObservers(DEAL_ID);
	}

	public String getDescription() {
		return mDescription;
	}

	public void setDescription(String description) {
		mDescription = description;
		setChanged();
		notifyObservers(DESCRIPTION);
	}

	public long getEndTimeMillis() {
		return mEndTimeMillis;
	}

	public void setEndTimeMillis(long endTimeMillis) {
		mEndTimeMillis = endTimeMillis;
		setChanged();
		notifyObservers(END_TIME);
	}

	public String getFulfillmentUrl() {
		return mFulfillmentUrl;
	}

	public void setFulfillmentUrl(String fulfillmentUrl) {
		mFulfillmentUrl = fulfillmentUrl;
		setChanged();
		notifyObservers(FULFILLMENT_URL);
	}

	public String getImageUrl() {
		final String imageUrl;
		if (mThumbnailImage.matches(GROUPON_REGEX)) {
			imageUrl = mThumbnailImage.replaceAll(GROUPON_REMOVE_THUMBNAIL_REGEX, "");
		} else {
			imageUrl = mThumbnailImage;
		}
		return imageUrl;
	}

	public String getLabel() {
		return mLabel;
	}

	public void setLabel(String label) {
		mLabel = label;
		setChanged();
		notifyObservers(LABEL);
	}

	public String getSource() {
		return mSource;
	}

	public void setSource(String source) {
		mSource = source;
		setChanged();
		notifyObservers(SOURCE);
	}

	public long getStartTimeMillis() {
		return mStartTimeMillis;
	}

	public void setStartTimeMillis(long startTimeMillis) {
		mStartTimeMillis = startTimeMillis;
		setChanged();
		notifyObservers(START_TIME);
	}

	public String getThumbnailImage() {
		return mThumbnailImage;
	}

	public String getThumbnailImage(GrouponThumbnailSize size) {
		if (size != null && mThumbnailImage.matches(GROUPON_REGEX)) {
			return mThumbnailImage.replaceAll(GROUPON_REPLACE_THUMBNAIL_SIZE_REGEX, String.format("c%dx%d", size.mSize, size.mSize));
		}
		return mThumbnailImage;
	}

	public void setThumbnailImage(String thumbnailImage) {
		mThumbnailImage = thumbnailImage;
		setChanged();
		notifyObservers(THUMBNAIL_IMAGE);
	}

	public static enum GrouponThumbnailSize {
		SMALL(50),
		NORMAL(100),
		LARGE(200),
		XLARGE(300);

		private int mSize;

		private GrouponThumbnailSize(int size) {
			mSize = size;
		}

		public int getSize() {
			return mSize;
		}
	}
}
