package com.citymaps.mobile.android.http.volley;

import com.android.volley.*;
import com.android.volley.toolbox.HttpHeaderParser;
import com.citymaps.mobile.android.util.LogEx;
import com.google.gson.*;

import java.io.UnsupportedEncodingException;
import java.util.Map;

public abstract class GetGsonRequest<T> extends Request<T> {
	/**
	 * A {@link com.google.gson.Gson} instance used to generate the result.
	 */
	protected static Gson sGson;

	protected static JsonParser sJsonParser;

	protected final Class<T> mClass;
	private final Map<String, String> mHeaders;
	private final Response.Listener<T> mListener;

	/**
	 * Convenience method to get the static {@link com.google.gson.Gson} instance.
	 * @return The static Gson instance.
	 */
	protected Gson getGson() {
		if (sGson == null)
			sGson = new GsonBuilder()
					.setPrettyPrinting()
					.create();
		return sGson;
	}

	/**
	 * Make a GET request and return a parsed object from JSON.
	 *
	 * @param url URL of the request to make
	 * @param clazz Relevant class object, for Gson's reflection
	 * @param headers Map of request headers
	 */
	public GetGsonRequest(String url, Class<T> clazz, Map<String, String> headers,
						  Response.Listener<T> listener, Response.ErrorListener errorListener) {
		super(Method.GET, url, errorListener);
		setShouldCache(false);

		if (LogEx.isLoggable(LogEx.VERBOSE)) {
			LogEx.v(String.format("url=%s, headers=%s", url, headers));
		}

		mClass = clazz;
		mHeaders = headers;
		mListener = listener;
	}

	@Override
	public Map<String, String> getHeaders() throws AuthFailureError {
		return mHeaders != null ? mHeaders : super.getHeaders();
	}

	@Override
	protected void deliverResponse(T response) {
		mListener.onResponse(response);
	}

	@Override
	protected Response<T> parseNetworkResponse(NetworkResponse response) {
		try {
			String json = new String(
					response.data,
					HttpHeaderParser.parseCharset(response.headers));

			if (LogEx.isLoggable(LogEx.VERBOSE)) {
				if (sJsonParser == null) {
					sJsonParser = new JsonParser();
				}
				LogEx.v(String.format("response=%s", getGson().toJson(sJsonParser.parse(json))));
			}

			return Response.success(
					getGson().fromJson(json, mClass),
					HttpHeaderParser.parseCacheHeaders(response));
		} catch (UnsupportedEncodingException e) {
			return Response.error(new ParseError(e));
		} catch (JsonSyntaxException e) {
			return Response.error(new ParseError(e));
		}
	}
}
