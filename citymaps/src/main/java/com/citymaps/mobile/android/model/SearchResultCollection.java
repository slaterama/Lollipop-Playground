package com.citymaps.mobile.android.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SearchResultCollection extends SearchResult {

	public static final String MAP_ID = "map_id";

	public static final String CATEGORIES = "categories";
	public static final String COVER_IMAGE_URL = "cover_image_url";
	public static final String DESCRIPTION = "description";
	public static final String EDITORS = "editors";
	public static final String IS_SAVED = "is_saved";
	public static final String LIKES = "likes";
	public static final String MARKER_NAME = "marker_name";
	public static final String NUM_MARKERS = "num_markers";
	public static final String NUM_SAVES = "num_saves";
	public static final String OWNER_AVATAR = "owner_avatar";
	public static final String OWNER_DISPLAY_NAME = "owner_display_name";
	public static final String OWNER_USERNAME = "owner_username";
	public static final String USER_ID = "user_id";

	@SerializedName(MAP_ID)
	private String mMapId;

	@SerializedName(CATEGORIES)
	private String[] mCategories;

	@SerializedName(COVER_IMAGE_URL)
	private String mCoverImageUrl;

	@SerializedName(DESCRIPTION)
	private String mDescription;

	@SerializedName(EDITORS)
	private List<User> mEditors;

	@SerializedName(IS_SAVED)
	private boolean mSaved;

	@SerializedName(LIKES)
	private int mLikes;

	@SerializedName(MARKER_NAME)
	private String mMarkerName;

	@SerializedName(NUM_MARKERS)
	private int mNumMarkers;

	@SerializedName(NUM_SAVES)
	private int mNumSaves;

	@SerializedName(OWNER_AVATAR)
	private String mOwnerAvatar;

	@SerializedName(OWNER_DISPLAY_NAME)
	private String mOwnerDisplayName;

	@SerializedName(OWNER_USERNAME)
	private String mOwnerUsername;

	@SerializedName(USER_ID)
	private String mUserId;

	@Override
	public void setId(String id) {
		super.setId(id);
		mMapId = id;
		setChanged();
		notifyObservers(MAP_ID);
	}

	public String getMapId() {
		return mMapId;
	}

	public void setMapId(String mapId) {
		setId(mapId);
	}

	public String[] getCategories() {
		return mCategories;
	}

	public void setCategories(String[] categories) {
		mCategories = categories;
		setChanged();
		notifyObservers(CATEGORIES);
	}

	public String getCoverImageUrl() {
		return mCoverImageUrl;
	}

	public void setCoverImageUrl(String coverImageUrl) {
		mCoverImageUrl = coverImageUrl;
		setChanged();
		notifyObservers(COVER_IMAGE_URL);
	}

	public String getDescription() {
		return mDescription;
	}

	public void setDescription(String description) {
		mDescription = description;
		setChanged();
		notifyObservers(DESCRIPTION);
	}

	public List<User> getEditors() {
		return mEditors;
	}

	public void setEditors(List<User> editors) {
		mEditors = editors;
		setChanged();
		notifyObservers(EDITORS);
	}

	public boolean isSaved() {
		return mSaved;
	}

	public void setSaved(boolean saved) {
		mSaved = saved;
		setChanged();
		notifyObservers(IS_SAVED);
	}

	public int getLikes() {
		return mLikes;
	}

	public void setLikes(int likes) {
		mLikes = likes;
		setChanged();
		notifyObservers(LIKES);
	}

	public String getMarkerName() {
		return mMarkerName;
	}

	public void setMarkerName(String markerName) {
		mMarkerName = markerName;
		setChanged();
		notifyObservers(MARKER_NAME);
	}

	public int getNumMarkers() {
		return mNumMarkers;
	}

	public void setNumMarkers(int numMarkers) {
		mNumMarkers = numMarkers;
		setChanged();
		notifyObservers(NUM_MARKERS);
	}

	public int getNumSaves() {
		return mNumSaves;
	}

	public void setNumSaves(int numSaves) {
		mNumSaves = numSaves;
		setChanged();
		notifyObservers(NUM_SAVES);
	}

	public String getOwnerAvatar() {
		return mOwnerAvatar;
	}

	public void setOwnerAvatar(String ownerAvatar) {
		mOwnerAvatar = ownerAvatar;
		setChanged();
		notifyObservers(OWNER_AVATAR);
	}

	public String getOwnerDisplayName() {
		return mOwnerDisplayName;
	}

	public void setOwnerDisplayName(String ownerDisplayName) {
		mOwnerDisplayName = ownerDisplayName;
		setChanged();
		notifyObservers(OWNER_DISPLAY_NAME);
	}

	public String getOwnerUsername() {
		return mOwnerUsername;
	}

	public void setOwnerUsername(String ownerUsername) {
		mOwnerUsername = ownerUsername;
		setChanged();
		notifyObservers(OWNER_USERNAME);
	}

	@Override
	public ObjectType getType() {
		return ObjectType.COLLECTION;
	}

	public String getUserId() {
		return mUserId;
	}

	public void setUserId(String userId) {
		mUserId = userId;
		setChanged();
		notifyObservers(USER_ID);
	}
}
