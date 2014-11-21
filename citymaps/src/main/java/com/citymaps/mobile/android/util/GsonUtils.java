package com.citymaps.mobile.android.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public final class GsonUtils {

	private static final String DATE_FORMAT_PATTERN = "yyyy-MM-dd'T'HH:mm:ss'Z'";

	/**
	 * A {@link com.google.gson.Gson} instance used to generate the result.
	 */
	private static Gson sGson;

	/**
	 * Convenience method to get the static {@link com.google.gson.Gson} instance.
	 * @return The static Gson instance.
	 */
	public static Gson getGson() {
		if (sGson == null)
			sGson = new GsonBuilder()
					.setDateFormat(DATE_FORMAT_PATTERN)
					.setPrettyPrinting()
					.create();
		return sGson;
	}

	private GsonUtils() {
	}
}
