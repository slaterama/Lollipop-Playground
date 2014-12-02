package com.citymaps.mobile.android.util;

import android.text.TextUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

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
					.registerTypeAdapter(String[].class, new StringArrayAdapter().nullSafe())
					.setPrettyPrinting()
					.create();
		return sGson;
	}

	private GsonUtils() {
	}

	protected static class StringArrayAdapter extends TypeAdapter<String[]> {
		@Override
		public void write(JsonWriter out, String[] value) throws IOException {
			if (value == null) {
				out.nullValue();
				return;
			}

			out.value(TextUtils.join(",", value));
		}

		@Override
		public String[] read(JsonReader in) throws IOException {
			if (in.peek() == JsonToken.NULL) {
				in.nextNull();
				return null;
			}

			String categories = in.nextString();
			return categories.split(",");
		}
	}
}
