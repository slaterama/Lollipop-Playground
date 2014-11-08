package com.citymaps.mobile.android.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public final class GsonUtils {

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
					.setPrettyPrinting()
					.create();
		return sGson;
	}

	private GsonUtils() {
	}
}
