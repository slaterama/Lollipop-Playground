package com.citymaps.mobile.android.modelnew.volley;

import com.android.volley.*;
import com.android.volley.toolbox.HttpHeaderParser;
import com.citymaps.mobile.android.util.GsonUtils;
import com.citymaps.mobile.android.util.LogEx;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import java.io.UnsupportedEncodingException;
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
			LogEx.v(String.format("url=%s", url));
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

	@Override
	protected Response<T> parseNetworkResponse(NetworkResponse response) {
		try {
			JsonObject jsonObject = newJsonObject(response);
			if (LogEx.isLoggable(LogEx.VERBOSE)) {
				LogEx.v(String.format("response=%s", GsonUtils.getGson().toJson(jsonObject)));
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
		if (volleyError.networkResponse == null || !(volleyError instanceof ServerError)) {
			return super.parseNetworkError(volleyError);
		} else try {
			JsonObject jsonObject = newJsonObject(volleyError.networkResponse);
			if (LogEx.isLoggable(LogEx.ERROR)) {
				LogEx.e(String.format("error=%s", GsonUtils.getGson().toJson(jsonObject)));
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
			Gson gson = GsonUtils.getGson();
			T result = gson.fromJson(jsonObject, mClass);
			return Response.success(result, HttpHeaderParser.parseCacheHeaders(response));
		} catch (JsonSyntaxException e) {
			return Response.error(new ParseError(e));
		}
	}

	protected VolleyError processParsedNetworkError(VolleyError volleyError, JsonObject jsonObject) {
		return volleyError;
	}
}
