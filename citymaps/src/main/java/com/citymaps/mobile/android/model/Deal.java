package com.citymaps.mobile.android.model;

import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.Observable;

public class Deal extends Observable {

	private static final String DEAL_ID = "deal_id";
	private static final String LABEL = "label";
	private static final String DESCRIPTION = "description";
	private static final String THUMBNAIL_IMAGE = "thumbnail_image";
	private static final String FULFILLMENT_URL = "fulfillment_url";
	private static final String SOURCE = "source";
	private static final String END_TIME = "end_time";

	@SerializedName(DEAL_ID)
	private String mId;

	@SerializedName(DESCRIPTION)
	private String mDescription;

	@SerializedName(END_TIME)
	private Date mEndTime;

	@SerializedName(FULFILLMENT_URL)
	private String mFulfillmentUrl;

	@SerializedName(LABEL)
	private String mLabel;

	@SerializedName(SOURCE)
	private String mSource;

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

	public Date getEndTime() {
		return mEndTime;
	}

	public void setEndTime(Date endTime) {
		mEndTime = endTime;
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

	public String getThumbnailImage() {
		return mThumbnailImage;
	}

	public void setThumbnailImage(String thumbnailImage) {
		mThumbnailImage = thumbnailImage;
		setChanged();
		notifyObservers(THUMBNAIL_IMAGE);
	}
}
