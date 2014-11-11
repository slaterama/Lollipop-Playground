package com.citymaps.mobile.android.model;

import com.android.volley.*;
import com.android.volley.toolbox.HttpHeaderParser;
import com.citymaps.mobile.android.util.GsonUtils;
import com.citymaps.mobile.android.util.LogEx;
import com.google.gson.*;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class GsonRequest<T> extends Request<T> {

	protected static JsonParser sJsonParser;

	protected final Class<T> mClass;
	private Map<String, String> mHeaders;
	private Map<String, String> mParams;
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
	 /**
	 * Make a GET request and return a parsed object from JSON.
	 *
	 * @param method The method of the request to make.
	 * @param url URL of the request to make.
	 * @param clazz Relevant class object, for Gson's reflection.
	 * @param listener
	 * @param errorListener
	 */
	public GsonRequest(int method, String url, Class<T> clazz,
					   Response.Listener<T> listener, Response.ErrorListener errorListener) {
		super(method, url, errorListener);
		setShouldCache(false);

		if (LogEx.isLoggable(LogEx.VERBOSE)) {
			LogEx.v(String.format("url=%s", url));
		}

		mClass = clazz;
		mListener = listener;
	}

	public void putHeaders(Map<String, String> headers) {
		if (mHeaders == null) {
			mHeaders = new HashMap<String, String>(headers.size());
		}
		mParams.putAll(headers);
	}

	@Override
	public Map<String, String> getHeaders() throws AuthFailureError {
		return mHeaders != null ? mHeaders : super.getHeaders();
	}

	public void putParams(Map<String, String> params) {
		if (mParams == null) {
			mParams = new HashMap<String, String>(params.size());
		}
		mParams.putAll(params);
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
