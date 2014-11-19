package com.citymaps.mobile.android.util;

import android.net.Uri;
import android.text.TextUtils;
import com.google.android.gms.plus.model.people.Person;

import java.util.Set;

public class GoogleUtils {

	private static final String QUERY_PARAMETER_SIZE = "sz";

	/* Get various fields from Person */

	public static String getFirstName(Person person) {
		try {
			return person.getName().getGivenName();
		} catch (NullPointerException e) {
			return null;
		}
	}

	public static String getLastName(Person person) {
		try {
			return person.getName().getFamilyName();
		} catch (NullPointerException e) {
			return null;
		}
	}

	public static String getUsername(String accountName) {
		try {
			return accountName.split("@")[0];
		} catch (NullPointerException e) {
			return null;
		} catch (IndexOutOfBoundsException e) {
			return null;
		}
	}

	/* Get avatar URL using base URL */

	private static String getAvatarUrl(String baseUrl, int size) {
		try {
			Uri uri = Uri.parse(baseUrl);
			Uri.Builder builder = uri.buildUpon().clearQuery();
			Set<String> parameterNames = uri.getQueryParameterNames();
			for (String name : parameterNames) {
				if (!TextUtils.equals(name, QUERY_PARAMETER_SIZE)) {
					builder.appendQueryParameter(name, uri.getQueryParameter(name));
				}
			}
			if (size > 0) {
				builder.appendQueryParameter(QUERY_PARAMETER_SIZE, String.valueOf(size));
			}
			return builder.toString();
		} catch (NullPointerException e) {
			return null;
		}
	}

	/* Get avatar URL using Person */

	public static String getAvatarUrl(Person person, int size) {
		try {
			return getAvatarUrl(person.getImage().getUrl(), size);
		} catch (NullPointerException e) {
			return null;
		}
	}

	public static String getAvatarUrl(Person person) {
		return getAvatarUrl(person, 0);
	}

	public static String getAvatarUrl(Person person, PictureType type) {
		return getAvatarUrl(person, type == null ? 0 : type.getSize());
	}

	/*
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

	public static String getFirstName(Person person) {
		return (person == null || person.getName() == null ? null : person.getName().getGivenName());
	}

	public static String getLastName(Person person) {
		return (person == null || person.getName() == null ? null : person.getName().getFamilyName());
	}

	public static String getBaseAvatarUrl(Person person) {
		String avatarUrl = null;
		if (person != null) {
			Person.Image image = person.getImage();
			if (image != null) {
				String url = image.getUrl();
				if (url != null) {
					avatarUrl = UriUtils.removeParameter(url, "sz");
				}
			}
		}
		return avatarUrl;
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
	*/

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
