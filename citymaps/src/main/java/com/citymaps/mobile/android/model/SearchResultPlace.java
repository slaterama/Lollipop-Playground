package com.citymaps.mobile.android.model;

import android.content.Context;
import com.citymaps.citymapsengine.LonLat;
import com.citymaps.mobile.android.app.SessionManager;
import com.citymaps.mobile.android.config.Endpoint;
import com.citymaps.mobile.android.config.Environment;
import com.google.gson.annotations.SerializedName;

public class SearchResultPlace extends SearchResult {

	public static final String BUSINESS_ID = "business_id";

	public static final String ACTIVITY = "activity";
	public static final String ADDRESS = "address";
	public static final String ANALYTICS_PARTNERS = "analytics_partners";
	public static final String ATTRIBUTION = "attribution";
	public static final String CATEGORIES = "categories";
	public static final String CATEGORY_ICON = "category_icon";
	public static final String CATEGORY_ID = "category_id";
	public static final String CATEGORY_NAME = "category_name";
	public static final String CATEGORY_SHORT_NAME = "category_short_name";
	public static final String CITY = "city";
	public static final String DEALS = "deals";
	public static final String EXPLORE_SCORE = "explore_score";
	public static final String IS_CLOSED = "is_closed";
	public static final String IS_LIKED = "is_liked";
	public static final String LAT = "lat";
	public static final String LOCATION = "location";
	public static final String LOGO_IMAGE = "logo_image";
	public static final String LON = "lon";
	public static final String OPENTABLE = "opentable";
	public static final String PERSONALIZED = "personalized";
	public static final String PHONE = "phone";
	public static final String PRICE = "price";
	public static final String RATING = "rating";
	public static final String RATING_0_5 = "rating_0_5";
	public static final String TAGS = "tags";
	public static final String VISIBILITY = "visibility";

	@SerializedName(BUSINESS_ID)
	private String mBusinessId;

	@SerializedName(ACTIVITY)
	private Object mActivity;

	@SerializedName(ADDRESS)
	private String mAddress;

	@SerializedName(ANALYTICS_PARTNERS)
	private int mAnalyticsPartners;

	@SerializedName(ATTRIBUTION)
	private String mAttribution;

	@SerializedName(CATEGORIES)
	private String[] mCategories;

	@SerializedName(CATEGORY_ICON)
	private int mCategoryIcon;

	@SerializedName(CATEGORY_ID)
	private String mCategoryId;

	@SerializedName(CATEGORY_NAME)
	private String mCategoryName;

	@SerializedName(CATEGORY_SHORT_NAME)
	private String mCategoryShortName;

	@SerializedName(CITY)
	private String mCity;

	@SerializedName(IS_CLOSED)
	private boolean mClosed;

	@SerializedName(DEALS)
	private Deal[] mDeals;

	@SerializedName(EXPLORE_SCORE)
	private int mExploreScore;

	@SerializedName(LAT)
	private double mLat;

	@SerializedName(IS_LIKED)
	private boolean mLiked;

	private LonLat mLocation;

	@SerializedName(LOGO_IMAGE)
	private int mLogoImage;

	@SerializedName(LON)
	private double mLon;

	@SerializedName(OPENTABLE)
	private String mOpenTable;

	@SerializedName(PERSONALIZED)
	private boolean mPersonalized;

	@SerializedName(PHONE)
	private String mPhone;

	@SerializedName(PRICE)
	private int mPrice;

	@SerializedName(RATING)
	private float mRating;

	@SerializedName(RATING_0_5)
	private float mRatingZeroFive;

	@SerializedName(TAGS)
	private String[] mTags;

	@SerializedName(VISIBILITY)
	private int mVisibility;

	@Override
	public void setId(String id) {
		super.setId(id);
		mBusinessId = id;
		setChanged();
		notifyObservers(BUSINESS_ID);
	}

	public String getBusinessId() {
		return mBusinessId;
	}

	public void setBusinessId(String businessId) {
		setId(businessId);
	}

	public Object getActivity() {
		return mActivity;
	}

	public void setActivity(Object activity) {
		mActivity = activity;
		setChanged();
		notifyObservers(ACTIVITY);
	}

	public String getAddress() {
		return mAddress;
	}

	public void setAddress(String address) {
		mAddress = address;
		setChanged();
		notifyObservers(ADDRESS);
	}

	public int getAnalyticsPartners() {
		return mAnalyticsPartners;
	}

	public void setAnalyticsPartners(int analyticsPartners) {
		mAnalyticsPartners = analyticsPartners;
		setChanged();
		notifyObservers(ANALYTICS_PARTNERS);
	}

	public String getAttribution() {
		return mAttribution;
	}

	public void setAttribution(String attribution) {
		mAttribution = attribution;
		setChanged();
		notifyObservers(ATTRIBUTION);
	}

	public String[] getCategories() {
		return mCategories;
	}

	public void setCategories(String[] categories) {
		mCategories = categories;
		setChanged();
		notifyObservers(CATEGORIES);
	}

	public int getCategoryIcon() {
		return mCategoryIcon;
	}

	public void setCategoryIcon(int categoryIcon) {
		mCategoryIcon = categoryIcon;
		setChanged();
		notifyObservers(CATEGORY_ICON);
	}

	public String getCategoryId() {
		return mCategoryId;
	}

	public void setCategoryId(String categoryId) {
		mCategoryId = categoryId;
		setChanged();
		notifyObservers(CATEGORY_ID);
	}

	public String getCategoryName() {
		return mCategoryName;
	}

	public void setCategoryName(String categoryName) {
		mCategoryName = categoryName;
		setChanged();
		notifyObservers(CATEGORY_NAME);
	}

	public String getCategoryShortName() {
		return mCategoryShortName;
	}

	public void setCategoryShortName(String categoryShortName) {
		mCategoryShortName = categoryShortName;
		setChanged();
		notifyObservers(CATEGORY_SHORT_NAME);
	}

	public String getCity() {
		return mCity;
	}

	public void setCity(String city) {
		mCity = city;
		setChanged();
		notifyObservers(CITY);
	}

	public boolean isClosed() {
		return mClosed;
	}

	public void setClosed(boolean closed) {
		mClosed = closed;
		setChanged();
		notifyObservers(IS_CLOSED);
	}

	public Deal[] getDeals() {
		return mDeals;
	}

	public void setDeals(Deal[] deals) {
		mDeals = deals;
		setChanged();
		notifyObservers(DEALS);
	}

	public int getExploreScore() {
		return mExploreScore;
	}

	public void setExploreScore(int exploreScore) {
		mExploreScore = exploreScore;
		setChanged();
		notifyObservers(EXPLORE_SCORE);
	}

	public void setLat(double lat) {
		mLat = lat;
		if (mLocation == null) {
			mLocation = new LonLat(mLon, mLat);
		} else {
			mLocation.latitude = mLat;
		}
		setChanged();
		notifyObservers(LOCATION);
	}

	public boolean isLiked() {
		return mLiked;
	}

	public void setLiked(boolean liked) {
		mLiked = liked;
		setChanged();
		notifyObservers(IS_LIKED);
	}

	public LonLat getLocation() {
		return mLocation;
	}

	public void setLocation(LonLat location) {
		if (location == null) {
			mLon = 0.0d;
			mLat = 0.0d;
		} else {
			mLon = location.longitude;
			mLat = location.latitude;
		}
		mLocation = location;
		setChanged();
		notifyObservers(LOCATION);
	}

	public int getLogoImage() {
		return mLogoImage;
	}

	public void setLogoImage(int logoImage) {
		mLogoImage = logoImage;
		setChanged();
		notifyObservers(LOGO_IMAGE);
	}

	public String getLogoImageUrl(Context context) {
		Environment environment = SessionManager.getInstance(context).getEnvironment();
		return environment.buildUrlString(Endpoint.Type.PLACE_ICON, mLogoImage);
	}

	public void setLon(double lon) {
		mLon = lon;
		if (mLocation == null) {
			mLocation = new LonLat(mLon, mLat);
		} else {
			mLocation.longitude = mLon;
		}
		setChanged();
		notifyObservers(LOCATION);
	}

	public String getOpenTable() {
		return mOpenTable;
	}

	public void setOpenTable(String openTable) {
		mOpenTable = openTable;
		setChanged();
		notifyObservers(OPENTABLE);
	}

	public boolean isPersonalized() {
		return mPersonalized;
	}

	public void setPersonalized(boolean personalized) {
		mPersonalized = personalized;
		setChanged();
		notifyObservers(PERSONALIZED);
	}

	public String getPhone() {
		return mPhone;
	}

	public void setPhone(String phone) {
		mPhone = phone;
		setChanged();
		notifyObservers(PHONE);
	}

	public int getPrice() {
		return mPrice;
	}

	public void setPrice(int price) {
		mPrice = price;
		setChanged();
		notifyObservers(PRICE);
	}

	public float getRating() {
		return mRating;
	}

	public void setRating(float rating) {
		mRating = rating;
		setChanged();
		notifyObservers(RATING);
	}

	public float getRatingZeroFive() {
		return mRatingZeroFive;
	}

	public void setRatingZeroFive(float ratingZeroFive) {
		mRatingZeroFive = ratingZeroFive;
		setChanged();
		notifyObservers(RATING_0_5);
	}

	public String[] getTags() {
		return mTags;
	}

	public void setTags(String[] tags) {
		mTags = tags;
		setChanged();
		notifyObservers(TAGS);
	}

	@Override
	public ObjectType getType() {
		return ObjectType.PLACE;
	}

	public int getVisibility() {
		return mVisibility;
	}

	public void setVisibility(int visibility) {
		mVisibility = visibility;
		setChanged();
		notifyObservers(VISIBILITY);
	}
}
