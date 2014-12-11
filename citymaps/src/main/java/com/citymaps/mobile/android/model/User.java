package com.citymaps.mobile.android.model;

import android.content.Context;
import android.text.TextUtils;
import com.citymaps.mobile.android.R;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * A user of the Citymaps application.
 */
@SuppressWarnings("unused")
public class User extends CitymapsObservable {

	/**
	 * Whether this user is active.
	 */
	@SerializedName("is_active")
	private boolean mActive;

	/**
	 * The admin level of this user.
	 */
	@SerializedName("admin_level")
	private int mAdminLevel;

	/**
	 * The provider that supplied the avatar for this user.
	 */
	@SerializedName("avatar_provider")
	private AvatarProvider mAvatarProvider;

	/**
	 * The URL for this user's avatar.
	 */
	@SerializedName("avatar_url")
	private String mAvatarUrl;

	/**
	 * This user's bio (i.e. a short description of this user).
	 */
	@SerializedName("bio")
	private String mBio;

	/**
	 * The city in which this user is located.
	 */
	@SerializedName("city")
	private String mCity;

	/**
	 * The token assigned to this user by the Citymaps framework.
	 */
	@SerializedName("citymaps_token")
	private String mCitymapsToken;

	/**
	 * The total number of collections (maps) this user has added.
	 */
	@SerializedName("map_adds")
	private int mCollectionAdds;

	/**
	 * The total number of collections (maps) this user has liked.
	 */
	@SerializedName("map_likes")
	private int mCollectionLikes;

	/**
	 * The total number of collections (maps) this user has saved.
	 */
	@SerializedName("map_saves")
	private int mCollectionSaves;

	/**
	 * The total number of collections (maps) this user has created.
	 */
	@SerializedName("maps_count")
	private int mCollectionsCount;

	/**
	 * The total number of collections (maps) this user has saved.
	 * <i>Duplicate of {@link #mCollectionSaves}.</i>
	 */
	@SerializedName("maps_saved_count")
	private int mCollectionsSavedCount;

	/**
	 * The name of this user if the user is a company (i.e.
	 * {@link #mDisplayCompany} is set to {@code true}.
	 */
	@SerializedName("company_name")
	private String mCompanyName;

	/**
	 * Whether to use {@link #mCompanyName} as this user's display name.
	 */
	@SerializedName("display_company_name")
	private boolean mDisplayCompany;

	/**
	 * The user's email address.
	 */
	@SerializedName("email_address")
	private String mEmail;

	/**
	 * Whether this user is a Facebook friend of the current user (if a user is currently logged in).
	 */
	@SerializedName("is_facebook_friend")
	private boolean mFacebookFriend;

	/**
	 * The user's first name.
	 */
	@SerializedName("first_name")
	private String mFirstName;

	/**
	 * Whether or not this user is being followed by the current user (if a user is currently logged in).
	 */
	@SerializedName("is_followed)")
	private boolean mFollowed;

	/**
	 * The total number of users that are following this user.
	 */
	@SerializedName("followers_count")
	private int mFollowersCount;

	/**
	 * The total number of users that this user is following.
	 */
	@SerializedName("follows_count")
	private int mFollowsCount;

	/**
	 * The unique id assigned to this user.
	 */
	@SerializedName("user_id")
	private String mId;

	/**
	 * The number of profile images associated with this user.
	 */
	@SerializedName("images_count")
	private int mImagesCount;

	/**
	 * The date of this user's last login.
	 */
	@SerializedName("last_login")
	private Date mLastLogin;

	/**
	 * This user's last name.
	 */
	@SerializedName("last_name")
	private String mLastName;

	/**
	 * This user's login IP address.
	 */
	@SerializedName("login_ip")
	private String mLoginIpAddress;

	/**
	 * The total number of places in all of this user's collections (maps).
	 */
	@SerializedName("markers_count")
	private int mMarkersCount;

	/**
	 * This user's display name.
	 */
	@SerializedName("name")
	private String mName;

	/**
	 * The total number of places this user has added.
	 */
	@SerializedName("business_adds")
	private int mPlaceAdds;

	/**
	 * The total number of places this user has commented on.
	 */
	@SerializedName("business_comments")
	private int mPlaceComments;

	/**
	 * The total number of places this user has liked.
	 */
	@SerializedName("business_likes")
	private int mPlaceLikes;

	/**
	 * The total number of places this user has liked.
	 * <i>Duplicate of {@link #mPlaceLikes}.</i>
	 */
	@SerializedName("business_likes_count")
	private int mPlacesLikedCount;

	/**
	 * The template to display as the default header background for this user if
	 * {@link #mPostcardUrl} does not contain a value.
	 */
	@SerializedName("postcard_template")
	private PostcardTemplate mPostcardTemplate;

	/**
	 * The URL of the image to display as the default header background for this user.
	 */
	@SerializedName("postcard_url")
	private String mPostcardUrl;

	/**
	 * Whether this user is public.
	 */
	@SerializedName("is_public")
	private boolean mPublic;

	/**
	 * Whether this user is a rollout user.
	 */
	@SerializedName("is_rollout_user")
	private boolean mRolloutUser;

	/**
	 * The source of this user, when this user was returned as part of a search.
	 */
	@SerializedName("source_id")
	private int mSourceId;

	/**
	 * The state in which this user is located.
	 */
	@SerializedName("state")
	private String mState;

	/**
	 * Whether this user is a super administrator.
	 */
	@SerializedName("is_super_admin")
	private boolean mSuperAdmin;

	/**
	 * The timestamp of the most decent entry in this user's activity stream.
	 */
	@SerializedName("stream_timestamp")
	private Date mStreamTimestamp;

	/**
	 * A class that contains all of the third-party credentials associated with this user.
	 */
	@SerializedName("third_party_credentials")
	private ThirdPartyCredentials mThirdPartyCredentials;

	/**
	 * The Facebook ID associated with this user.
	 * <i>Deprecated. Use {@link #mThirdPartyCredentials} instead.</i>
	 */
	@Deprecated
	@SerializedName("third_party_id")
	private String mThirdPartyId;

	/**
	 * The total number of tips this user has left.
	 */
	@SerializedName("tips_count")
	private int mTipsCount;

	/**
	 * The username chosen by this user.
	 */
	@SerializedName("username")
	private String mUsername;

	/**
	 * This user's website.
	 */
	@SerializedName("website")
	private String mWebsite;

	/**
	 * @return {@link CitymapsObject.ObjectType#USER}.
	 */
	@Override
	public ObjectType getType() {
		return ObjectType.USER;
	}

	/**
	 * Gets the ID of this object.
	 * @return The ID of this object.
	 */
	@Override
	public String getId() {
		return mId;
	}

	/**
	 * Sets the ID of this user.
	 * @param id The new ID.
	 */
	@Override
	public void setId(String id) {
		mId = id;
	}

	/**
	 * Gets the display name of this user.
	 * @return This user's display name.
	 */
	@Override
	public String getName() {
		return getName(null);
	}

	/**
	 * Returns this user's display name.
	 * @return The display name, optionally using a Context to resolve the full name format.
	 */
	public String getName(Context context) {
		if (!TextUtils.isEmpty(mName)) {
			return mName;
		} else if (mDisplayCompany && !TextUtils.isEmpty(mCompanyName)) {
			return mCompanyName;
		} else if (TextUtils.isEmpty(mFirstName)) {
			return mLastName;
		} else if (TextUtils.isEmpty(mLastName)) {
			return mFirstName;
		} else if (context == null) {
			return String.format("%s %s", mFirstName, mLastName);
		} else {
			return context.getString(R.string.user_full_name_format, mFirstName, mLastName);
		}
	}

	/**
	 * Sets this user's display name.
	 * @param name The new name.
	 */
	@Override
	public void setName(String name) {
		mName = name;
	}

	/**
	 * Returns whether this user is active.
	 * @return Whether this user is active.
	 */
	public boolean isActive() {
		return mActive;
	}

	/**
	 * Sets whether this user is active.
	 * @param active Whether this user is active.
	 */
	public void setActive(boolean active) {
		mActive = active;
	}

	/**
	 * Gets the admin level for this user.
	 * @return The admin level.
	 */
	public int getAdminLevel() {
		return mAdminLevel;
	}

	/**
	 * Sets the admin level for this user.
	 * @param adminLevel The new admin level.
	 */
	public void setAdminLevel(int adminLevel) {
		mAdminLevel = adminLevel;
	}

	/**
	 * Gets the provider that supplied the avatar for this user.
	 * @return The avatar provider.
	 */
	public AvatarProvider getAvatarProvider() {
		return mAvatarProvider;
	}

	/**
	 * Sets the provider that supplied the avatar for this user.
	 * @param avatarProvider The new provider.
	 */
	public void setAvatarProvider(AvatarProvider avatarProvider) {
		mAvatarProvider = avatarProvider;
	}

	/**
	 * Gets the URL for this user's avatar.
	 * @return The avatar URL.
	 */
	public String getAvatarUrl() {
		return mAvatarUrl;
	}

	/**
	 * Sets the URL for this user's avatar.
	 * @param avatarUrl The new URL.
	 */
	public void setAvatarUrl(String avatarUrl) {
		mAvatarUrl = avatarUrl;
	}

	/**
	 * Returns this user's bio (i.e. a short description of this user).
	 * @return The bio.
	 */
	public String getBio() {
		return mBio;
	}

	/**
	 * Sets this user's bio (i.e. a short description of this user).
	 * @param bio The new bio.
	 */
	public void setBio(String bio) {
		mBio = bio;
	}

	/**
	 * Gets the city in which this user is located.
	 * @return The city.
	 */
	public String getCity() {
		return mCity;
	}

	/**
	 * Sets the city in which this user is located.
	 * @param city The new city.
	 */
	public void setCity(String city) {
		mCity = city;
	}

	/**
	 * Gets the token assigned to this user by the Citymaps framework.
	 * @return The Citymaps token.
	 */
	public String getCitymapsToken() {
		return mCitymapsToken;
	}

	/**
	 * Sets the token assigned to this user by the Citymaps framework.
	 * @param citymapsToken The new Citymaps token.
	 */
	public void setCitymapsToken(String citymapsToken) {
		mCitymapsToken = citymapsToken;
	}

	/**
	 * Gets the total number of collections (maps) this user has added.
	 * @return The total number of added collections.
	 */
	public int getCollectionAdds() {
		return mCollectionAdds;
	}

	/**
	 * Sets the total number of collections (maps) this user has added.
	 * @param adds The number of added collections.
	 */
	public void setCollectionAdds(int adds) {
		mCollectionAdds = adds;
	}

	/**
	 * Gets the total number of collections (maps) this user has liked.
	 * @return The total number of liked collections.
	 */
	public int getCollectionLikes() {
		return mCollectionLikes;
	}

	/**
	 * Sets the total number of collections (maps) this user has liked.
	 * @param likes The number of liked collections.
	 */
	public void setCollectionLikes(int likes) {
		mCollectionLikes = likes;
	}

	/**
	 * Gets the total number of collections (maps) this user has saved.
	 * @return The total number of saved collections.
	 */
	public int getCollectionSaves() {
		return mCollectionSaves;
	}

	/**
	 * Sets the total number of collections (maps) this user has saved.
	 * @param saves The number of saved collections.
	 */
	public void setCollectionSaves(int saves) {
		mCollectionSaves = saves;
		mCollectionsSavedCount = saves;
	}

	/**
	 * Gets the total number of collections (maps) this user has created.
	 * @return The number of collections.
	 */
	public int getCollectionsCount() {
		return mCollectionsCount;
	}

	/**
	 * Sets the total number of collections (maps) this user has created.
	 * @param count The new count.
	 */
	public void setCollectionsCount(int count) {
		mCollectionsCount = count;
	}

	/**
	 * Gets the total number of collections (maps) this user has saved.
	 * <i>Duplicate of {@link #getCollectionSaves}.</i>
	 * @return The total number of saved collections.
	 */
	public int getCollectionsSavedCount() {
		return mCollectionsSavedCount;
	}

	/**
	 * Sets the total number of collections (maps) this user has saved.
	 * <i>Duplicate of {@link #getCollectionSaves}.</i>
	 * @param saves The total number of saved collections.
	 */
	public void setCollectionsSavedCount(int saves) {
		mCollectionsSavedCount = saves;
		mCollectionSaves = saves;
	}

	/**
	 * Gets the name of this user if the user is a company (i.e.
	 * {@link #mDisplayCompany} is set to {@code true}.
	 * @return The company name.
	 */
	public String getCompanyName() {
		return mCompanyName;
	}

	/**
	 * Gets the name to display if this user is a company (i.e.
	 * {@link #mDisplayCompany} is set to {@code true}.
	 * @param companyName The new company name.
	 */
	public void setCompanyName(String companyName) {
		mCompanyName = companyName;
	}

	/**
	 * Gets whether to use this user's company name as the display name.
	 * @return <code>True</code> to use this user's company name as the display name,
	 * <code>false</code> otherwise.
	 */
	public boolean isDisplayCompany() {
		return mDisplayCompany;
	}

	/**
	 * Sets whether to use this user's company name as the display name.
	 * @param displayCompany Whether to use this user's company name as the display name.
	 */
	public void setDisplayCompany(boolean displayCompany) {
		mDisplayCompany = displayCompany;
	}

	/**
	 * Gets this user's email address.
	 * @return The email address.
	 */
	public String getEmail() {
		return mEmail;
	}

	/**
	 * Sets this user's email address.
	 * @param email The new email address.
	 */
	public void setEmail(String email) {
		mEmail = email;
	}

	/**
	 * Gets whether this user is a Facebook friend of the current user,
	 * or {@code false} if no user is currently logged in.
	 * @return Whether this user is a Facebook friend.
	 */
	public boolean isFacebookFriend() {
		return mFacebookFriend;
	}

	/**
	 * Sets whether this user is a Facebook friend of the current user.
	 * @param facebookFriend Whether this user is a Facebook friend.
	 */
	public void setFacebookFriend(boolean facebookFriend) {
		mFacebookFriend = facebookFriend;
	}

	/**
	 * Returns this user's first name.
	 * @return The first name.
	 */
	public String getFirstName() {
		return mFirstName;
	}

	/**
	 * Sets this user's first name.
	 * @param firstName The new name.
	 */
	public void setFirstName(String firstName) {
		mFirstName = firstName;
	}

	/**
	 * Gets whether this user is followed by the current user,
	 * or {@code false} if no user is currently logged in.
	 * @return Whether this user is followed by the current user.
	 */
	public boolean isFollowed() {
		return mFollowed;
	}

	/**
	 * Sets whether this user is followed by the current user.
	 * @param followed Whether this user is followed by the current user.
	 */
	public void setFollowed(boolean followed) {
		mFollowed = followed;
	}

	/**
	 * Gets the total number of users that are following this user.
	 * @return The number of followers.
	 */
	public int getFollowersCount() {
		return mFollowersCount;
	}

	/**
	 * Sets the total number of users that are following this user.
	 * @param count The new count.
	 */
	public void setFollowersCount(int count) {
		mFollowersCount = count;
	}

	/**
	 * Gets the total number of users that this user is following.
	 * @return The number of users.
	 */
	public int getFollowsCount() {
		return mFollowsCount;
	}

	/**
	 * Sets the total number of users that this user is following.
	 * @param count The new count.
	 */
	public void setFollowsCount(int count) {
		mFollowsCount = count;
	}

	/**
	 * Gets the number of profile images associated with this user.
	 * @return The number of images.
	 */
	public int getImagesCount() {
		return mImagesCount;
	}

	/**
	 * Sets the number of profile images associated with this user.
	 * @param count The new count.
	 */
	public void setImagesCount(int count) {
		mImagesCount = count;
	}

	/**
	 * Gets the date of this user's last login.
	 * @return The last login date.
	 */
	public Date getLastLogin() {
		return mLastLogin;
	}

	/**
	 * Sets the date of this user's last login.
	 * @param lastLogin The new login date.
	 */
	public void setLastLogin(Date lastLogin) {
		mLastLogin = lastLogin;
	}

	/**
	 * Returns this user's last name.
	 * @return The last name.
	 */
	public String getLastName() {
		return mLastName;
	}

	/**
	 * Sets this user's last name.
	 * @param lastName The new name.
	 */
	public void setLastName(String lastName) {
		mLastName = lastName;
	}

	/**
	 * Gets this user's login IP address.
	 * @return The IP address.
	 */
	public String getLoginIpAddress() {
		return mLoginIpAddress;
	}

	/**
	 * Sets this user's login IP address.
	 * @param address The IP address.
	 */
	public void setLoginIpAddress(String address) {
		mLoginIpAddress = address;
	}

	/**
	 * Gets the total number of places in all of this user's collections (maps).
	 * @return The number of places.
	 */
	public int getMarkersCount() {
		return mMarkersCount;
	}

	/**
	 * Sets the total number of places in all of this user's collections (maps).
	 * @param count The new count.
	 */
	public void setMarkersCount(int count) {
		mMarkersCount = count;
	}

	/**
	 * Gets the total number of places this user has added.
	 * @return The total number of added places.
	 */
	public int getPlaceAdds() {
		return mPlaceAdds;
	}

	/**
	 * Sets the total number of places this user has added.
	 * @param count The new count.
	 */
	public void setPlaceAdds(int count) {
		mPlaceAdds = count;
	}

	/**
	 * Gets the total number of places this user has commented on.
	 * @return The total number of places.
	 */
	public int getPlaceComments() {
		return mPlaceComments;
	}

	/**
	 * Sets the total number of places this user has commented on.
	 * @param count The new count.
	 */
	public void setPlaceComments(int count) {
		mPlaceComments = count;
	}

	/**
	 * Gets the total number of places this user has liked.
	 * @return The total number liked places.
	 */
	public int getPlaceLikes() {
		return mPlaceLikes;
	}

	/**
	 * Sets the total number of places this user has liked.
	 * @param count The new count.
	 */
	public void setPlaceLikes(int count) {
		mPlaceLikes = count;
		mPlacesLikedCount = count;
	}

	/**
	 * Gets the total number of places this user has liked.
	 * <i>Duplicate of {@link #getPlaceLikes}.</i>
	 * @return The total number of liked places.
	 */
	public int getPlacesLikedCount() {
		return mPlacesLikedCount;
	}

	/**
	 * Sets the total number of places this user has liked.
	 * <i>Duplicate of {@link #getPlaceLikes}.</i>
	 * @param count The new count.
	 */
	public void setPlacesLikedCount(int count) {
		mPlacesLikedCount = count;
		mPlaceLikes = count;
	}

	/**
	 * Gets the template to display as the default header background for this user if
	 * {@link #mPostcardUrl} does not contain a value.
	 * @return The postcard template.
	 */
	public PostcardTemplate getPostcardTemplate() {
		return mPostcardTemplate;
	}

	/**
	 * Sets the template to display as the default header background for this user if
	 * {@link #mPostcardUrl} does not contain a value.
	 * @param template The new postcard template.
	 */
	public void setPostcardTemplate(PostcardTemplate template) {
		mPostcardTemplate = template;
	}

	/**
	 * Gets the URL of the image to display as the default header background for this user.
	 * @return The postcard URL.
	 */
	public String getPostcardUrl() {
		return mPostcardUrl;
	}

	/**
	 * Sets the URL of the image to display as the default header background for this user.
	 * @param url The new postcard URL.
	 */
	public void setPostcardUrl(String url) {
		mPostcardUrl = url;
	}

	/**
	 * Gets whether this user is public.
	 * @return Whether this user is public.
	 */
	public boolean isPublic() {
		return mPublic;
	}

	/**
	 * Sets whether this user is public.
	 * @param publicUser Whether this user is a public user.
	 */
	public void setPublic(boolean publicUser) {
		mPublic = publicUser;
	}

	/**
	 * Gets whether this user is a rollout user.
	 * @return Whether this user is a rollout user.
	 */
	public boolean isRolloutUser() {
		return mRolloutUser;
	}

	/**
	 * Sets whether this user is a rollout user.
	 * @param rolloutUser Whether this user is a rollout user.
	 */
	public void setRolloutUser(boolean rolloutUser) {
		mRolloutUser = rolloutUser;
	}

	/**
	 * Gets the id of the source that returned this user, if this user was returned as part of a search.
	 * @return The source ID.
	 */
	public int getSourceId() {
		return mSourceId;
	}

	/**
	 * Sets the id of the source that returned this user, if this user was returned as part of a search.
	 * @param sourceId The source ID.
	 */
	public void setSourceId(int sourceId) {
		mSourceId = sourceId;
	}

	/**
	 * Gets the state in which this user is located.
	 * @return The state.
	 */
	public String getState() {
		return mState;
	}

	/**
	 * Sets the state in which this user is located.
	 * @param state The new state.
	 */
	public void setState(String state) {
		mState = state;
	}

	/**
	 * Gets whether this user is a super administrator.
	 * @return Whether this user is a super administrator.
	 */
	public boolean isSuperAdmin() {
		return mSuperAdmin;
	}

	/**
	 * Sets whether this user is a super administrator.
	 * @param superAdmin Whether this user is a super administrator.
	 */
	public void setSuperAdmin(boolean superAdmin) {
		mSuperAdmin = superAdmin;
	}

	/**
	 * Gets the timestamp of the most decent entry in this user's activity stream.
	 * @return The stream timestamp.
	 */
	public Date getStreamTimestamp() {
		return mStreamTimestamp;
	}

	/**
	 * Sets the timestamp of the most decent entry in this user's activity stream.
	 * @param timestamp The new timestamp.
	 */
	public void setStreamTimestamp(Date timestamp) {
		mStreamTimestamp = timestamp;
	}

	/**
	 * Gets the third-party credentials associated with this user.
	 * @return The third-party credentials.
	 */
	public ThirdPartyCredentials getThirdPartyCredentials() {
		return mThirdPartyCredentials;
	}

	/**
	 * Sets the third-party credentials associated with this user.
	 * @param credentials The new credentials.
	 */
	public void setThirdPartyCredentials(ThirdPartyCredentials credentials) {
		mThirdPartyCredentials = credentials;
	}

	/**
	 * Gets the Facebook ID associated with this user.
	 * <i>Deprecated. Use {@link #getThirdPartyCredentials} instead.</i>
	 * @return The Facebook ID.
	 */
	@SuppressWarnings("deprecation")
	@Deprecated
	public String getThirdPartyId() {
		return mThirdPartyId;
	}

	/**
	 * Sets the Facebook ID associated with this user.
	 * <i>Deprecated. Use {@link #setThirdPartyCredentials} instead.</i>
	 * @param thirdPartyId The new ID.
	 */
	@SuppressWarnings("deprecation")
	public void setThirdPartyId(String thirdPartyId) {
		mThirdPartyId = thirdPartyId;
	}

	/**
	 * Gets the total number of tips this user has left.
	 * @return The total number of tips.
	 */
	public int getTipsCount() {
		return mTipsCount;
	}

	/**
	 * Sets the total number of tips this user has left.
	 * @param count The new count.
	 */
	public void setTipsCount(int count) {
		mTipsCount = count;
	}

	/**
	 * Gets the username chosen by this user.
	 * @return The username.
	 */
	public String getUsername() {
		return mUsername;
	}

	/**
	 * Sets the username chosen by this user.
	 * @param username The new username.
	 */
	public void setUsername(String username) {
		mUsername = username;
	}

	/**
	 * Gets this user's website.
	 * @return The website.
	 */
	public String getWebsite() {
		return mWebsite;
	}

	/**
	 * Sets this user's website.
	 * @param website The new website.
	 */
	public void setWebsite(String website) {
		mWebsite = website;
	}

	/**
	 * Helper method to get the Facebook ID associated with user, if one exists.
	 * @return The Facebook ID.
	 */
	public String getFacebookId() {
		String id = null;
		if (mThirdPartyCredentials != null) {
			ThirdPartyCredential credential = mThirdPartyCredentials.getFacebook();
			if (credential != null) {
				id = credential.mId;
			}
		}
		return id;
	}

	/**
	 * Helper method to get the Google ID associated with user, if one exists.
	 * @return The Google ID.
	 */
	public String getGoogleId() {
		String id = null;
		if (mThirdPartyCredentials != null) {
			ThirdPartyCredential credential = mThirdPartyCredentials.getGoogle();
			if (credential != null) {
				id = credential.mId;
			}
		}
		return id;
	}

	/**
	 * Indicates whether some other user is "equal to" this one.
	 * @param obj The reference user with which to compare.
	 * @return <code>True</code> if this user is the same as the obj argument; <code>false</code> otherwise.
	 */
	@Override
	public boolean equals(Object obj) {
		return (obj instanceof User && TextUtils.equals(mId, ((User) obj).getId()));
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	/**
	 * A class that represents a single third-party credential.
	 */
	public static class ThirdPartyCredential {

		/**
		 * The ID associated with this third-party credential.
		 */
		@SerializedName("id")
		private String mId;

		/**
		 * Gets the ID associated with this third-party credential.
		 * @return The third-party ID.
		 */
		public String getId() {
			return mId;
		}

		/**
		 * Sets the ID associated with this third-party credential.
		 * @param id The new ID.
		 */
		public void setId(String id) {
			mId = id;
		}
	}

	/**
	 * A class that contains a user's third party credentials.
	 */
	public static class ThirdPartyCredentials {

		/**
		 * This user's Facebook credientials.
		 */
		@SerializedName("facebook")
		private ThirdPartyCredential mFacebook;

		/**
		 * This user's Google credentials.
		 */
		@SerializedName("google")
		private ThirdPartyCredential mGoogle;

		/**
		 * Returns this user's Facebook credentials.
		 */
		public ThirdPartyCredential getFacebook() {
			return mFacebook;
		}

		/**
		 * Sets this user's Facebook credentials.
		 * @param credential The new Facebook credentials.
		 */
		public void setFacebook(ThirdPartyCredential credential) {
			mFacebook = credential;
		}

		/**
		 * Returns this user's Google credentials.
		 */
		public ThirdPartyCredential getGoogle() {
			return mGoogle;
		}

		/**
		 * Sets this user's Google credentials.
		 * @param credential The new Google credentials.
		 */
		public void setGoogle(ThirdPartyCredential credential) {
			mGoogle = credential;
		}
	}

	/**
	 * Enum type that specifies the provider that supplied a user's avatar.
	 */
	public static enum AvatarProvider {
		@SerializedName("0")
		CITYMAPS(0),

		@SerializedName("1")
		FACEBOOK(1),

		@SerializedName("2")
		GOOGLE(2);

		private int mIntValue;

		private AvatarProvider(int intValue) {
			mIntValue = intValue;
		}

		public int getIntValue() {
			return mIntValue;
		}
	}

	public static enum PostcardTemplate {
		@SerializedName("Default")
		DEFAULT("Default", R.drawable.pc_01_berry),

		@SerializedName("Cloud")
		CLOUD ("Cloud", R.drawable.pc_02_cloud),

		@SerializedName("Glass")
		GLASS("Glass", R.drawable.pc_03_glass),

		@SerializedName("Sky")
		SKY("Sky", R.drawable.pc_04_sky),

		@SerializedName("Sea")
		SEA("Sea", R.drawable.pc_05_sea),

		@SerializedName("Rust")
		RUST("Rust", R.drawable.pc_06_rust),

		@SerializedName("Slate")
		SLATE("Slate", R.drawable.pc_07_slate),

		@SerializedName("Madison Avenue")
		MADISON_AVENUE("Madison Avenue", R.drawable.pc_01_madison),

		@SerializedName("Dumbo")
		DUMBO("Dumbo", R.drawable.pc_02_dumbo),

		@SerializedName("Roller Coaster")
		ROLLER_COASTER("Roller Coaster", R.drawable.pc_03_santacruz_coaster),

		@SerializedName("Desert Road")
		DESERT("Desert Road", R.drawable.pc_04_desert),

		@SerializedName("Mountain Ave")
		MOUNTAIN_AVE("Mountain Ave", R.drawable.pc_05_mountain_ave),

		@SerializedName("Empire")
		EMPIRE("Empire", R.drawable.pc_06_empire),

		@SerializedName("Jersey Shore")
		JERSEY_SHORE("Jersey Shore", R.drawable.pc_07_jerseyshore),

		@SerializedName("Flower")
		FLOWER("Flower", R.drawable.pc_08_flower),

		@SerializedName("Skyline")
		SKYLINE("Skyline", R.drawable.pc_09_skyline);

		private static Map<String, PostcardTemplate> mNameMap;

		public static PostcardTemplate fromName(String name) {
			if (mNameMap == null) {
				PostcardTemplate[] templates = values();
				mNameMap = new HashMap<String, PostcardTemplate>(templates.length);
				for (PostcardTemplate template : templates) {
					mNameMap.put(template.mName, template);
				}
			}
			return mNameMap.get(name);
		}

		private String mName;
		private int mResId;

		private PostcardTemplate(String name, int resId) {
			mName = name;
			mResId = resId;
		}

		public String getName() {
			return mName;
		}

		public int getResId() {
			return mResId;
		}

		@Override
		public String toString() {
			return mName;
		}
	}
}
