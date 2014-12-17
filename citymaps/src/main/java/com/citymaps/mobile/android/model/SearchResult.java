package com.citymaps.mobile.android.model;

import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang3.builder.ToStringBuilder;

@SuppressWarnings("SpellCheckingInspection")
public class SearchResult extends CitymapsObservable {

	public static final String OBJECT_ID = "object_id";

	public static final String FOURSQUARE_ID = "foursquare_id";
	public static final String NAME = "name";
	public static final String SCORE = "score";
	public static final String TYPE = "type";

	@SerializedName(OBJECT_ID)
	private String mId;

	@SerializedName(FOURSQUARE_ID)
	private String mFoursquareId;

	private String mFoursquarePhotoUrl;

	@SerializedName(NAME)
	private String mName;

	@SerializedName(SCORE)
	private int mScore;

	@SerializedName(TYPE)
	private ObjectType mType;

	@Override
	public String getId() {
		return mId;
	}

	@Override
	public void setId(String id) {
		mId = id;
		setChanged();
		notifyObservers(OBJECT_ID);
	}

	public String getFoursquareId() {
		return mFoursquareId;
	}

	public void setFoursquareId(String foursquareId) {
		mFoursquareId = foursquareId;
		setChanged();
		notifyObservers(FOURSQUARE_ID);
	}

	public String getFoursquarePhotoUrl() {
		return mFoursquarePhotoUrl;
	}

	public void setFoursquarePhotoUrl(String foursquarePhotoUrl) {
		mFoursquarePhotoUrl = foursquarePhotoUrl;
	}

	@Override
	public String getName() {
		return mName;
	}

	@Override
	public void setName(String name) {
		mName = name;
		setChanged();
		notifyObservers(NAME);
	}

	public int getScore() {
		return mScore;
	}

	public void setScore(int score) {
		mScore = score;
		setChanged();
		notifyObservers(SCORE);
	}

	@Override
	public ObjectType getType() {
		return mType;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
