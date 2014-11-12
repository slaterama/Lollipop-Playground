package com.citymaps.mobile.android.model.volley;

import com.android.volley.*;
import com.android.volley.toolbox.HttpHeaderParser;
import com.citymaps.mobile.android.model.vo.ResultWrapper;
import com.citymaps.mobile.android.util.GsonUtils;
import com.citymaps.mobile.android.util.LogEx;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.UnsupportedEncodingException;

public class WrappedRequest<T, W extends ResultWrapper<T>> extends GsonRequest<T> {

	private Class<W> mWrapperClass;

	public WrappedRequest(int method, String url, Class<T> clazz, Class<W> wrapperClass,
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
			W wrapper = gson.fromJson(json, mWrapperClass);
			return Response.success(wrapper.getResult(), HttpHeaderParser.parseCacheHeaders(response));
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
				W wrapper = gson.fromJson(json, mWrapperClass);
				String message = wrapper.getMessage();
				return new VolleyError(message, volleyError);
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
