package com.citymaps.mobile.android.model;

import com.android.volley.*;
import com.android.volley.toolbox.HttpHeaderParser;
import com.citymaps.mobile.android.util.GsonUtils;
import com.citymaps.mobile.android.util.LogEx;
import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

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
	 *
	 * @return The static Gson instance.
	 */
	protected static JsonParser getJsonParser() {
		if (sJsonParser == null) {
			sJsonParser = new JsonParser();
		}
		return sJsonParser;
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

	@Override
	protected Response<T> parseNetworkResponse(NetworkResponse response) {
		try {
			String json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
			Gson gson = GsonUtils.getGson();
			if (LogEx.isLoggable(LogEx.VERBOSE)) {
				LogEx.v(String.format("response=%s", gson.toJson(getJsonParser().parse(json))));
			}
			T result = gson.fromJson(json, mClass);
			return Response.success(result, HttpHeaderParser.parseCacheHeaders(response));
		} catch (UnsupportedEncodingException e) {
			return Response.error(new ParseError(e));
		} catch (JsonSyntaxException e) {
			return Response.error(new ParseError(e));
		}
	}

	@Override
	protected VolleyError parseNetworkError(VolleyError volleyError) {
		if (volleyError instanceof ServerError && volleyError.networkResponse != null) {
			try {
				String json = new String(volleyError.networkResponse.data,
						HttpHeaderParser.parseCharset(volleyError.networkResponse.headers));
				Gson gson = GsonUtils.getGson();
				if (LogEx.isLoggable(LogEx.VERBOSE)) {
					LogEx.v(String.format("response=%s", gson.toJson(getJsonParser().parse(json))));
				}
				return super.parseNetworkError(volleyError);
			} catch (UnsupportedEncodingException e) {
				return super.parseNetworkError(volleyError);
			} catch (JsonSyntaxException e) {
				return super.parseNetworkError(volleyError);
			}
		} else {
			return super.parseNetworkError(volleyError);
		}
	}
}
