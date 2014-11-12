package com.citymaps.mobile.android.model;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.citymaps.mobile.android.util.GsonUtils;
import com.citymaps.mobile.android.util.LogEx;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.UnsupportedEncodingException;

public abstract class GsonWrappedRequest<T, W extends ResultWrapper<T>> extends GsonRequest<T> {

	protected final Class<W> mWrapperClass;

	public GsonWrappedRequest(int method, String url, Class<T> clazz, Class<W> wrapperClass,
							  Response.Listener<T> listener, Response.ErrorListener errorListener) {
		super(method, url, clazz, listener, errorListener);
		mWrapperClass = wrapperClass;
	}

	@Override
	protected Response<T> parseNetworkResponse(NetworkResponse response) {
		try {
			String json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
			Gson gson = GsonUtils.getGson();
			if (LogEx.isLoggable(LogEx.VERBOSE)) {
				LogEx.v(String.format("response=%s", gson.toJson(getJsonParser().parse(json))));
			}
			W result = gson.fromJson(json, mWrapperClass);
			return Response.success(result.getResult(), HttpHeaderParser.parseCacheHeaders(response));
		} catch (UnsupportedEncodingException e) {
			return Response.error(new ParseError(e));
		} catch (JsonSyntaxException e) {
			return Response.error(new ParseError(e));
		}
	}

	@Override
	protected VolleyError parseNetworkError(VolleyError volleyError) {
		try {
			NetworkResponse response = volleyError.networkResponse;
			String json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
			Gson gson = GsonUtils.getGson();
			if (LogEx.isLoggable(LogEx.VERBOSE)) {
				LogEx.v(String.format("response=%s", gson.toJson(getJsonParser().parse(json))));
			}
			W error = gson.fromJson(json, mWrapperClass);
			VolleyError ve = new VolleyError(error.getMessage(), volleyError);
			return ve;
		} catch (UnsupportedEncodingException e) {
			return super.parseNetworkError(volleyError);
		} catch (JsonSyntaxException e) {
			return super.parseNetworkError(volleyError);
		}
	}
}
