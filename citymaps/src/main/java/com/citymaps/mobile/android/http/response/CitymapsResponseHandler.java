package com.citymaps.mobile.android.http.response;

import com.citymaps.mobile.android.app.ThrowableWrapper;
import com.citymaps.mobile.android.app.Wrapper;
import com.citymaps.mobile.android.util.LogEx;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Base ResponseHandler for handling Citymaps API requests.
 *
 * @param <D> The data type of the result expected from calling the HTTP request. This result
 *            will be wrapped in a {@link Wrapper} before being
 *            returned.
 */
public abstract class CitymapsResponseHandler<D> implements ResponseHandler<Wrapper<D, Exception>> {

	/**
	 * A {@link com.google.gson.Gson} instance used to generate the result.
	 */
	protected static Gson sGson;

	/**
	 * A {@link com.google.gson.JsonParser} instance used to parse the result.
	 */
	protected static JsonParser sJsonParser;

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

	@Override
	public Wrapper<D, Exception> handleResponse(HttpResponse httpResponse) throws IOException {

		final StatusLine statusLine = httpResponse.getStatusLine();
		int statusCode = statusLine.getStatusCode();
		if (statusCode >= 300)
			return new ThrowableWrapper<D, Exception>(
					new HttpResponseException(statusCode, statusLine.getReasonPhrase()));

		final HttpEntity entity = httpResponse.getEntity();
		InputStream inputStream = entity.getContent();

		if (sJsonParser == null)
			sJsonParser = new JsonParser();

		InputStreamReader reader = new InputStreamReader(inputStream);
		JsonElement json = sJsonParser.parse(reader);
		if (LogEx.isLoggable(LogEx.VERBOSE)) {
			String response = getGson().toJson(json);
			LogEx.v(String.format("response=%s", response));
		}

		return wrapResult(json);
	}

	/**
	 * Get the result to return in {@link #handleResponse(org.apache.http.HttpResponse)} given the
	 * {@link com.google.gson.Gson} instance and {@link java.io.InputStreamReader} instance.
	 *
	 * @param json The JsonElement that contains the content resulting from the HTTP request.
	 * @return The result (or Exception) wrapped in a {@link Wrapper} object.
	 */
	protected abstract Wrapper<D, Exception> wrapResult(JsonElement json);
}