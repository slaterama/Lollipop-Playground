package com.citymaps.mobile.android.model;

import com.android.volley.*;
import com.android.volley.toolbox.HttpHeaderParser;
import com.citymaps.mobile.android.util.GsonUtils;
import com.citymaps.mobile.android.util.LogEx;
import com.google.gson.*;

import java.io.UnsupportedEncodingException;
import java.util.Map;

public abstract class GsonRequest<T> extends Request<T> {

	protected static JsonParser sJsonParser;

	protected final Class<T> mClass;
	private final Map<String, String> mHeaders;
	private final Map<String, String> mParams;
	private final Response.Listener<T> mListener;

	/**
	 * Convenience method to get the static {@link com.google.gson.Gson} instance.
	 * @return The static Gson instance.
	 */
	protected static JsonParser getJsonParser() {
		if (sJsonParser == null) {
			sJsonParser = new JsonParser();
		}
		return sJsonParser;
	}

	/**
	 * Make a GET request and return a parsed object from JSON.
	 *
	 * @param method The method of the request to make.
	 * @param url URL of the request to make.
	 * @param clazz Relevant class object, for Gson's reflection.
	 * @param headers Map of request headers.
	 */
	public GsonRequest(int method, String url, Class<T> clazz,
					   Map<String, String> headers, Map<String, String> params,
					   Response.Listener<T> listener, Response.ErrorListener errorListener) {
		super(method, url, errorListener);
		setShouldCache(false);

		if (LogEx.isLoggable(LogEx.VERBOSE)) {
			LogEx.v(String.format("url=%s, headers=%s", url, headers));
		}

		mClass = clazz;
		mHeaders = headers;
		mParams = params;
		mListener = listener;
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

	protected <I> Response<I> parseNetworkResponse(NetworkResponse response, Class<I> clazz) {
		try {
			String json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
			Gson gson = GsonUtils.getGson();
			if (LogEx.isLoggable(LogEx.VERBOSE)) {
				LogEx.v(String.format("response=%s", gson.toJson(getJsonParser().parse(json))));
			}
			return Response.success(gson.fromJson(json, clazz), HttpHeaderParser.parseCacheHeaders(response));
		} catch (UnsupportedEncodingException e) {
			return Response.error(new ParseError(e));
		} catch (JsonSyntaxException e) {
			return Response.error(new ParseError(e));
		}
	}

	@Override
	protected Response<T> parseNetworkResponse(NetworkResponse response) {
		return parseNetworkResponse(response, mClass);
	}
}
