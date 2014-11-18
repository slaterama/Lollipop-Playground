package com.citymaps.mobile.android.model;

import android.support.annotation.NonNull;
import com.citymaps.mobile.android.util.ThirdPartyUtils;
import com.facebook.model.GraphUser;
import com.google.android.gms.plus.model.people.Person;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class ThirdPartyUser {

	private static final String PROPERTY_NAME_EMAIL = "email";

	private static String emptyIfNull(Object value) {
		return (value == null ? "" : value.toString());
	}

	ThirdParty mThirdParty;
	String mToken;
	String mId;
	String mFirstName;
	String mLastName;
	String mUsername;
	String mEmail;
	String mAvatarUrl;

	public ThirdPartyUser(@NonNull ThirdParty thirdParty, @NonNull String token, @NonNull String id) {
		mThirdParty = thirdParty;
		mToken = token;
		mId = id;
	}

	public ThirdPartyUser(@NonNull String token, @NonNull GraphUser user) {
		this(ThirdParty.FACEBOOK, token, user.getId());
		mFirstName = user.getFirstName();
		mLastName = user.getLastName();
		mUsername = user.getUsername();
		mEmail = ThirdPartyUtils.getEmail(user);
		mAvatarUrl = ThirdPartyUtils.getBaseAvatarUrl(user);
	}

	public ThirdPartyUser(@NonNull String token, @NonNull Person person, String email) {
		this(ThirdParty.GOOGLE, token, person.getId());
		mFirstName = ThirdPartyUtils.getFirstName(person);
		mLastName = ThirdPartyUtils.getLastName(person);
		mUsername = ThirdPartyUtils.getUsernameFromEmail(email);
		mEmail = email;
		mAvatarUrl = ThirdPartyUtils.getBaseAvatarUrl(person);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
