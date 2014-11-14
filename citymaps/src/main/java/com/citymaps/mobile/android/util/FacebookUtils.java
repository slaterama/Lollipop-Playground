package com.citymaps.mobile.android.util;

import android.net.Uri;

import java.util.Arrays;
import java.util.List;

public class FacebookUtils {

	public static final String[] FACEBOOK_READ_PERMISSIONS = new String[] {
			"public_profile", "email", "user_friends"
	};

	public static final List<String> FACEBOOK_READ_PERMISSIONS_LIST;
	static {
		FACEBOOK_READ_PERMISSIONS_LIST = Arrays.asList(FACEBOOK_READ_PERMISSIONS);
	}

	private static String getAvatarUrl(String id, int width, int height, PictureType type, boolean invalidate) {
		Uri.Builder builder = Uri.parse(String.format("http://graph.facebook.com/%s/picture", id)).buildUpon();
		if (width > 0 && height > 0) {
			builder.appendQueryParameter("width", String.valueOf(width))
					.appendQueryParameter("height", String.valueOf(height));
		} else if (type != null) {
			builder.appendQueryParameter("type", type.toString().toLowerCase());
		}

		if (invalidate) {
			builder.appendQueryParameter("timestamp", String.valueOf(System.currentTimeMillis()));
		}
		return builder.toString();
	}

	public static String getAvatarUrl(String id, int width, int height, boolean invalidate) {
		return getAvatarUrl(id, width, height, null, invalidate);
	}

	public static String getAvatarUrl(String id, int width, int height) {
		return getAvatarUrl(id, width, height, null, true);
	}

	public static String getAvatarUrl(String id, PictureType type, boolean invalidate) {
		return getAvatarUrl(id, -1, -1, type, invalidate);
	}

	public static String getAvatarUrl(String id, PictureType type) {
		return getAvatarUrl(id, -1, -1, type, true);
	}

	public static String getAvatarUrl(String id, boolean invalidate) {
		return getAvatarUrl(id, -1, -1, PictureType.NORMAL, invalidate);
	}

	public static String getAvatarUrl(String id) {
		return getAvatarUrl(id, -1, -1, PictureType.NORMAL, true);
	}

	private FacebookUtils() {
	}

	public static enum PictureType {
		SQUARE,
		SMALL,
		NORMAL,
		LARGE
	}
}
