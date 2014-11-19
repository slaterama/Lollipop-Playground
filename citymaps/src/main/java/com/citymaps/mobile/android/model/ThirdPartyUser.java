package com.citymaps.mobile.android.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import com.citymaps.mobile.android.util.FacebookUtils;
import com.citymaps.mobile.android.util.GoogleUtils;
import com.facebook.model.GraphUser;
import com.google.android.gms.plus.model.people.Person;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class ThirdPartyUser implements Parcelable {

	public static final Creator<ThirdPartyUser> CREATOR = new Creator<ThirdPartyUser>() {
		@Override
		public ThirdPartyUser createFromParcel(Parcel in) {
			return new ThirdPartyUser(in);
		}

		@Override
		public ThirdPartyUser[] newArray(int size) {
			return new ThirdPartyUser[0];
		}
	};

	private ThirdParty mThirdParty;
	private String mToken;
	private String mId;
	private String mFirstName;
	private String mLastName;
	private String mUsername;
	private String mEmail;
	private String mAvatarUrl;

	public ThirdPartyUser(@NonNull ThirdParty thirdParty, @NonNull String token, @NonNull String id) {
		mThirdParty = thirdParty;
		mToken = token;
		mId = id;
	}

	public ThirdPartyUser(@NonNull String token, @NonNull GraphUser user) {
		this(ThirdParty.FACEBOOK, token, user.getId());
		mFirstName = user.getFirstName();
		mLastName = user.getLastName();
		mUsername = FacebookUtils.getUsername(user);
		mEmail = FacebookUtils.getEmail(user);
		mAvatarUrl = FacebookUtils.getAvatarUrl(user);
	}

	public ThirdPartyUser(@NonNull String token, @NonNull Person person, String accountName) {
		this(ThirdParty.GOOGLE, token, person.getId());
		mFirstName = GoogleUtils.getFirstName(person);
		mLastName = GoogleUtils.getLastName(person);
		mUsername = GoogleUtils.getUsername(accountName);
		mEmail = accountName;
		mAvatarUrl = GoogleUtils.getAvatarUrl(person);
	}

	private ThirdPartyUser(Parcel in) {
		mThirdParty = (ThirdParty) in.readSerializable();
		mToken = in.readString();
		mId = in.readString();
		mFirstName = in.readString();
		mLastName = in.readString();
		mUsername = in.readString();
		mEmail = in.readString();
		mAvatarUrl = in.readString();
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel out, int flags) {
		out.writeSerializable(mThirdParty);
		out.writeString(mToken);
		out.writeString(mId);
		out.writeString(mFirstName);
		out.writeString(mLastName);
		out.writeString(mUsername);
		out.writeString(mEmail);
		out.writeString(mAvatarUrl);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	public ThirdParty getThirdParty() {
		return mThirdParty;
	}

	public String getToken() {
		return mToken;
	}

	public String getId() {
		return mId;
	}

	public String getFirstName() {
		return mFirstName;
	}

	public String getLastName() {
		return mLastName;
	}

	public String getUsername() {
		return mUsername;
	}

	public String getEmail() {
		return mEmail;
	}

	public String getAvatarUrl() {
		return mAvatarUrl;
	}
}
