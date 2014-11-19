package com.citymaps.mobile.android.util;

import android.net.Uri;
import com.facebook.model.GraphUser;

public class FacebookUtils {

	private static final String AVATAR_URL_FORMAT = "http://graph.facebook.com/%s/picture";

	private static final String QUERY_PARAMETER_WIDTH = "width";
	private static final String QUERY_PARAMETER_HEIGHT = "height";
	private static final String QUERY_PARAMETER_TYPE = "type";
	private static final String QUERY_PARAMETER_TIMESTAMP = "timestamp";

	private static final String PROPERTY_NAME_EMAIL = "email";

	/* Get various fields from GraphUser */

	public static String getEmail(GraphUser user) {
		try {
			return user.getProperty(PROPERTY_NAME_EMAIL).toString();
		} catch (NullPointerException e) {
			return null;
		}
	}

	public static String getUsername(GraphUser user) {
		try {
			String username = user.getUsername();
			if (username == null) {
				return getEmail(user).split("@")[0];
			} else {
				return username;
			}
		} catch (NullPointerException e) {
			return null;
		} catch (IndexOutOfBoundsException e) {
			return null;
		}
	}

	/* Get avatar URL using base URL */

	public static String getAvatarUrl(String baseUrl, int width, int height, boolean invalidate) {
		if (baseUrl == null) {
			return null;
		} else {
			Uri.Builder builder = Uri.parse(baseUrl).buildUpon();
			if (width > 0) {
				builder.appendQueryParameter(QUERY_PARAMETER_WIDTH, String.valueOf(width));
			}
			if (height > 0) {
				builder.appendQueryParameter(QUERY_PARAMETER_HEIGHT, String.valueOf(height));
			}
			if (invalidate) {
				builder.appendQueryParameter(QUERY_PARAMETER_TIMESTAMP, String.valueOf(System.currentTimeMillis()));
			}
			return builder.toString();
		}
	}

	public static String getAvatarUrl(String baseUrl, int width, int height) {
		return getAvatarUrl(baseUrl, width, height, false);
	}

	public static String getAvatarUrl(String baseUrl, int size, boolean invalidate) {
		return getAvatarUrl(baseUrl, size, size, invalidate);
	}

	public static String getAvatarUrl(String baseUrl, int size) {
		return getAvatarUrl(baseUrl, size, size, false);
	}

	public static String getAvatarUrl(String baseUrl, PictureType type, boolean invalidate) {
		if (baseUrl == null) {
			return null;
		} else {
			Uri.Builder builder = Uri.parse(baseUrl).buildUpon();
			if (type != null) {
				builder.appendQueryParameter(QUERY_PARAMETER_TYPE, String.valueOf(type));
			}
			if (invalidate) {
				builder.appendQueryParameter(QUERY_PARAMETER_TIMESTAMP, String.valueOf(System.currentTimeMillis()));
			}
			return builder.toString();
		}
	}

	public static String getAvatarUrl(String baseUrl, PictureType type) {
		return getAvatarUrl(baseUrl, type, false);
	}

	public static String getAvatarUrl(String baseUrl, boolean invalidate) {
		return getAvatarUrl(baseUrl, null, invalidate);
	}

	/* Get avatar URL using GraphUser */

	public static String getAvatarUrl(GraphUser user, int width, int height, boolean invalidate) {
		return getAvatarUrl(user == null ? null : String.format(AVATAR_URL_FORMAT, user.getId()), width, height, invalidate);
	}

	public static String getAvatarUrl(GraphUser user, int width, int height) {
		return getAvatarUrl(user, width, height, false);
	}

	public static String getAvatarUrl(GraphUser user, int size, boolean invalidate) {
		return getAvatarUrl(user, size, size, invalidate);
	}

	public static String getAvatarUrl(GraphUser user, int size) {
		return getAvatarUrl(user, size, size, false);
	}

	public static String getAvatarUrl(GraphUser user, PictureType type, boolean invalidate) {
		return getAvatarUrl(user == null ? null : String.format(AVATAR_URL_FORMAT, user.getId()), type, invalidate);
	}

	public static String getAvatarUrl(GraphUser user, PictureType type) {
		return getAvatarUrl(user, type, false);
	}

	public static String getAvatarUrl(GraphUser user, boolean invalidate) {
		return getAvatarUrl(user, null, invalidate);
	}

	public static String getAvatarUrl(GraphUser user) {
		return getAvatarUrl(user, null, false);
	}

	private FacebookUtils() {
	}

	public static enum PictureType {
		SQUARE("square"),
		SMALL("small"),
		NORMAL("normal"),
		LARGE("large");

		private String mToString;

		private PictureType(String toString) {
			mToString = toString;
		}

		@Override
		public String toString() {
			return mToString;
		}
	}
}
