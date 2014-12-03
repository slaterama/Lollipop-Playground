package com.citymaps.mobile.android.model.request;

import com.android.volley.*;
import com.android.volley.toolbox.HttpHeaderParser;
import com.citymaps.mobile.android.util.LogEx;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;

@SuppressWarnings("SpellCheckingInspection")
public abstract class GsonRequest<T> extends Request<T> {

	protected static final String DATE_FORMAT_PATTERN = "yyyy-MM-dd'T'HH:mm:ss'Z'";

	protected static Gson sGson;

	protected static JsonParser sJsonParser;

	protected static GsonBuilder newDefaultGsonBuilder() {
		return new GsonBuilder()
				.setDateFormat(DATE_FORMAT_PATTERN)
				.setPrettyPrinting();
	}

	protected final Class<T> mClass;
	private Map<String, String> mHeaders;
	private Map<String, String> mParams;
	private final Response.Listener<T> mListener;

	/**
	 * Convenience method to get the static {@link com.google.gson.Gson} instance.
	 *
	 * @return The static Gson instance.
	 */
	protected static JsonParser getJsonParser() {
		if (sJsonParser == null) {
			sJsonParser = new JsonParser();
		}
		return sJsonParser;
	}

	protected static JsonObject newJsonObject(NetworkResponse response)
			throws UnsupportedEncodingException, JsonSyntaxException {
		String json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
		return getJsonParser().parse(json).getAsJsonObject();
	}

	/**
	 * /**
	 * Make a GET request and return a parsed object from JSON.
	 *
	 * @param method        The method of the request to make.
	 * @param url           URL of the request to make.
	 * @param clazz         Relevant class object, for Gson's reflection.
	 * @param listener
	 * @param errorListener
	 */
	public GsonRequest(int method, String url, Class<T> clazz,
					   Map<String, String> headers, Map<String, String> params,
					   Response.Listener<T> listener, Response.ErrorListener errorListener) {
		super(method, url, errorListener);
		setShouldCache(false);

		if (LogEx.isLoggable(LogEx.VERBOSE)) {
			LogEx.v(String.format("url=%s, headers=%s, params=%s", url, headers, params));
		}

		mClass = clazz;
		mHeaders = headers;
		mParams = params;
		mListener = listener;

		/*
		GsonBuilder builder = new GsonBuilder()
				.setDateFormat(DATE_FORMAT_PATTERN)
				.setPrettyPrinting();
		customizeGson(builder);
		mGson = builder.create();
		*/
	}

	@Override
	public Map<String, String> getHeaders() throws AuthFailureError {
		return mHeaders != null ? mHeaders : super.getHeaders();
	}

	@Override
	protected Map<String, String> getParams() throws AuthFailureError {
		return mParams != null ? mParams : super.getParams();
	}

	@Override
	protected void deliverResponse(T response) {
		mListener.onResponse(response);
	}

	@Override
	protected Response<T> parseNetworkResponse(NetworkResponse response) {
		try {
			JsonObject jsonObject = newJsonObject(response);
			if (LogEx.isLoggable(LogEx.VERBOSE)) {
				LogEx.v(String.format("response=%s", getGson().toJson(jsonObject)));
			}
			return processParsedNetworkResponse(response, jsonObject);
		} catch (UnsupportedEncodingException e) {
			return Response.error(new ParseError(e));
		} catch (JsonSyntaxException e) {
			return Response.error(new ParseError(e));
		}
	}

	@Override
	protected VolleyError parseNetworkError(VolleyError volleyError) {
		if (volleyError.networkResponse == null /* || !(volleyError instanceof ServerError)*/) {
			return super.parseNetworkError(volleyError);
		} else try {
			JsonObject jsonObject = newJsonObject(volleyError.networkResponse);
			if (LogEx.isLoggable(LogEx.WARN)) {
				LogEx.w(String.format("error=%s", getGson().toJson(jsonObject)));
			}
			return processParsedNetworkError(volleyError, jsonObject);
		} catch (UnsupportedEncodingException e) {
			return new ParseError(e);
		} catch (JsonSyntaxException e) {
			return new ParseError(e);
		}
	}

	protected Response<T> processParsedNetworkResponse(NetworkResponse response, JsonObject jsonObject) {
		try {
			Gson gson = getGson();
			T result = gson.fromJson(jsonObject, mClass);
			return Response.success(result, HttpHeaderParser.parseCacheHeaders(response));
		} catch (JsonSyntaxException e) {
			return Response.error(new ParseError(e));
		}
	}

	protected VolleyError processParsedNetworkError(VolleyError volleyError, JsonObject jsonObject) {
		return volleyError;
	}

	protected Gson getGson() {
		if (sGson == null) {
			sGson = newDefaultGsonBuilder().create();
		}
		return sGson;
	}
}
