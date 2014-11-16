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

		url = url.substring(0,
				url.length() - 2)
				+ size;

		return url;
	}

	private GoogleUtils() {
	}
}
