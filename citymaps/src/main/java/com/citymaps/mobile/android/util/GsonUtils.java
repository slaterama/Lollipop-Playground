package com.citymaps.mobile.android.util;

import com.citymaps.mobile.android.model.Deal;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Date;

@SuppressWarnings("SpellCheckingInspection")
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
//					.registerTypeAdapter(Date.class, new DateDeserializer())
					.registerTypeAdapter(String[].class, new StringsDeserializer())
					.registerTypeAdapter(Deal[].class, new DealsDeserializer())
//					.registerTypeAdapter(Date.class, new DateAdapterFactory().create())
					.registerTypeAdapterFactory(new DateTypeAdapterFactory())
					.setPrettyPrinting()
					.create();
		return sGson;
	}

	private GsonUtils() {
	}

	/*
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
	*/

	protected static class StringsDeserializer implements JsonDeserializer<String[]> {
		@Override
		public String[] deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			String[] strings = null;
			if (json.isJsonArray()) {
				JsonArray jsonArray = json.getAsJsonArray();
				int size = jsonArray.size();
				strings = new String[size];
				for (int i = 0; i < size; i++) {
					JsonElement jsonElement = jsonArray.get(i);
					strings[i] = jsonElement.getAsString();
				}
			} else if (json.isJsonPrimitive()) {
				JsonPrimitive jsonPrimitive = json.getAsJsonPrimitive();
				String value = jsonPrimitive.getAsString();
				strings = value.split(",");
			}
			return strings;
		}
	}

	protected static class DealsDeserializer implements JsonDeserializer<Deal[]> {
		@Override
		public Deal[] deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			Gson gson = getGson();
			Deal[] deals = null;
			if (json.isJsonArray()) {
				JsonArray jsonArray = json.getAsJsonArray();
				int size = jsonArray.size();
				deals = new Deal[size];
				for (int i = 0; i < size; i++) {
					JsonElement jsonElement = jsonArray.get(i);
					deals[i] = gson.fromJson(jsonElement, Deal.class);
				}
			} else if (json.isJsonObject()) {
				deals = new Deal[]{gson.fromJson(json, Deal.class)};
			}
			return deals;
		}
	}

	protected static class DateDeserializer implements JsonDeserializer<Date> {
		@Override
		public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			Date date = null;
			if (json.isJsonPrimitive()) {
				JsonPrimitive jsonPrimitive = json.getAsJsonPrimitive();
				if (jsonPrimitive.isString()) {
					date = null;
				} else if (jsonPrimitive.isNumber()) {
					date = new Date(jsonPrimitive.getAsLong());
				}
			}
			return date;
		}
	}

	protected static class DateTypeAdapterFactory implements TypeAdapterFactory {
		@SuppressWarnings("unchecked")
		@Override
		public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
			if (!Date.class.isAssignableFrom(type.getRawType())) {
				return null;
			}

			final TypeAdapter<Date> delegate = gson.getDelegateAdapter(this, TypeToken.get(Date.class));

			TypeAdapter<Date> adapter = new TypeAdapter<Date>() {
				@Override
				public void write(JsonWriter out, Date value) throws IOException {
					delegate.write(out, value);
				}

				@Override
				public Date read(JsonReader in) throws IOException {
					JsonToken jsonToken = in.peek();
					switch (jsonToken) {
						case NUMBER:
							long millis = in.nextLong();
							return new Date(millis);
						case STRING:
						default:
							return delegate.read(in);
					}
				}
			};

			return (TypeAdapter<T>) adapter;
		}
	}
}
