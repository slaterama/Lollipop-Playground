package com.citymaps.mobile.android.model;

import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang3.builder.ToStringBuilder;

@SuppressWarnings("SpellCheckingInspection")
public class SearchResult extends CitymapsObservable {

	public static final String OBJECT_ID = "object_id";

	public static final String NAME = "name";
	public static final String SCORE = "score";
	public static final String TYPE = "type";

	@SerializedName(OBJECT_ID)
	private String mId;

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
