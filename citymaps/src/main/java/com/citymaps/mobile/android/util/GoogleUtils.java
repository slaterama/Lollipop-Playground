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
