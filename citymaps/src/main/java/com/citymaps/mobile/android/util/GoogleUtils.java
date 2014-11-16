package com.citymaps.mobile.android.util;

import android.text.TextUtils;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

public class GoogleUtils {

	private static final Scope[] GOOGLE_SCOPES = new Scope[]{
			Plus.SCOPE_PLUS_LOGIN,
			Plus.SCOPE_PLUS_PROFILE
	};

	private static final String[] GOOGLE_AUTH_SCOPES = new String[] {
			Scopes.PLUS_LOGIN
	};

	public static String getScope() {
		return "oauth2:" + TextUtils.join(" ", GOOGLE_AUTH_SCOPES);
	}

	public static String getAvatarUrl(Person person, int size) {
		if (person == null)
			return "";
		String url = person.getImage().getUrl();
		url = url.replaceAll("sz=\\d+$", String.format("sz=%d", size));
		return url;
	}

	public static String getAvatarUrl(Person person, PictureType type) {
		return getAvatarUrl(person, type.getSize());
	}

	public static String getAvatarUrl(Person person) {
		return getAvatarUrl(person, PictureType.SMALL.getSize());
	}

	private GoogleUtils() {
	}

	public static enum PictureType {
		SMALL(50),
		NORMAL(100),
		LARGE(200);

		private int mSize;

		private PictureType(int size) {
			mSize = size;
		}

		public int getSize() {
			return mSize;
		}
	}
}
