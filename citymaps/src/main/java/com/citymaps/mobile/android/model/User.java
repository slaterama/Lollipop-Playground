package com.citymaps.mobile.android.model;

import android.content.Context;
import android.text.TextUtils;
import com.citymaps.mobile.android.R;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.Date;

/**
 * A user of the Citymaps application.
 */
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
	 * The user's first name.
	 */
	@SerializedName("first_name")
	private String mFirstName;

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
	private String mLoginIp;

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
	 * The total number of businesses this user has added.
	 * <i>NOTE: Not sure if we are using this.</i>
	 */
	@SerializedName("business_adds")
	private int mPlaceAdds;

	/**
	 * The total number of businesses this user has commented on.
	 */
	@SerializedName("business_comments")
	private int mPlaceComments;

	/**
	 * The total number of businesses this user has liked.
	 */
	@SerializedName("business_likes")
	private int mPlaceLikes;

	/**
	 * The total number of businesses this user has liked.
	 * <i>Duplicate of {@link #mPlaceLikes}.</i>
	 */
	@SerializedName("business_likes_count")
	private int mPlacesLikedCount;

	/**
	 * The template to display as the default header background for this user if
	 * {@link #mPostcardUrl} does not contain a value.
	 */
	@SerializedName("postcard_template")
	private String mPostcardTemplate;

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
	 * Whether this is a rollout user.
	 */
	@SerializedName("is_rollout_user")
	private boolean mRolloutUser;

	/**
	 * The state in which this user is located.
	 */
	@SerializedName("state")
	private String mState;

	/**
	 * Whether this user is a super admin.
	 */
	@SerializedName("is_super_admin")
	private boolean mSuperAdmin;

	/**
	 * The date of the most decent entry in this user's activity stream.
	 */
	@SerializedName("stream_timestamp")
	private Date mStreamTimestamp;

	/**
	 * A class that contains all of the third-party credentials associated with this user.
	 */
	@SerializedName("third_party_credentials")
	private ThirdPartyCredentials mThirdPartyCredentials;

	/**
	 * The Facebook id associated with this user.
	 * <i>Deprecated. Use {@link #mThirdPartyCredentials} instead.</i>
	 */
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
	 * Sets the unique id assigned to this user.
	 * @param id The unique id assigned to this user.
	 */
	@Override
	public void setId(String id) {
		mId = id;
	}

	/**
	 * @return The unique id assigned to this user.
	 */
	@Override
	public String getId() {
		return mId;
	}

	/**
	 * @return The user's display name.
	 */
	@Override
	public String getName() {
		return getName(null);
	}

	/**
	 * @return The user's display name, optionally using a Context to
	 * resolve the full name format.
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


	public boolean isActive() {
		return mActive;
	}

	public void setActive(boolean active) {
		mActive = active;
	}

	/**
	 * @return The token assigned to this user by the Citymaps framework.
	 */
	public String getCitymapsToken() {
		return mCitymapsToken;
	}

	/**
	 * Sets the token assigned to this user by the Citymaps framework.
	 * @param citymapsToken The token assigned to this user by the Citymaps framework.
	 */
	public void setCitymapsToken(String citymapsToken) {
		mCitymapsToken = citymapsToken;
	}

	/**
	 * @return The user's first name.
	 */
	public String getFirstName() {
		return mFirstName;
	}

	/**
	 * @param firstName The user's first name.
	 */
	public void setFirstName(String firstName) {
		mFirstName = firstName;
	}

	/**
	 * @return The user's last name.
	 */
	public String getLastName() {
		return mLastName;
	}

	/**
	 * @param lastName The user's last name.
	 */
	public void setLastName(String lastName) {
		mLastName = lastName;
	}

	/**
	 * @return The username chosen by this user.
	 */
	public String getUsername() {
		return mUsername;
	}

	/**
	 * Sets the username chosen by this user.
	 * @param username The username chosen by this user.
	 */
	public void setUsername(String username) {
		mUsername = username;
	}

	/**
	 * @return The user's email address.
	 */
	public String getEmail() {
		return mEmail;
	}

	/**
	 * Sets this user's email address.
	 * @param email The user's email address.
	 */
	public void setEmail(String email) {
		mEmail = email;
	}

	/**
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
	 * @return The user's company name.
	 */
	public String getCompanyName() {
		return mCompanyName;
	}

	/**
	 * Sets this user's company name.
	 * @param companyName The user's company name.
	 */
	public void setCompanyName(String companyName) {
		mCompanyName = companyName;
	}

	/**
	 * Indicates whether some other user is "equal to" this one.
	 * @param obj The reference user with which to compare.
	 * @return <code>True</code> if this user is the same as the obj argument; <code>false</code> otherwise.
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof User)
			return TextUtils.equals(mId, ((User) obj).getId());
		return super.equals(obj);
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
		 * The id associated with this third-party credential.
		 */
		@SerializedName("id")
		private String mId;

		/**
		 * Returns the id associated with this third-party credential.
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
	}
}
