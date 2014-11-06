package com.citymaps.mobile.android.http.request;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.http.AndroidHttpClient;
import com.citymaps.mobile.android.app.CitymapsConnectivityException;
import com.citymaps.mobile.android.app.CitymapsRuntimeException;
import com.citymaps.mobile.android.app.ThrowableWrapper;
import com.citymaps.mobile.android.app.Wrapper;
import com.citymaps.mobile.android.config.Api;
import com.citymaps.mobile.android.config.Endpoint;
import com.citymaps.mobile.android.confignew.Environment;
import com.citymaps.mobile.android.util.LogEx;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.params.HttpParams;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URLEncoder;

/**
 * The base Citymaps HttpGet class used to execute Citymaps HTTP requests.
 *
 * @param <D> The data type of the response returned by executing the request.
 */
public abstract class CitymapsHttpGet<D> extends HttpGet
		implements CitymapsHttpRequest {

	/**
	 * Convenience method for encoding UTF-8 strings.
	 *
	 * @return The UTF-8-encoded string.
	 */
	public static String encode(String s) {
		try {
			return URLEncoder.encode(s, UTF_8);
		} catch (UnsupportedEncodingException e) {
			return s;
		}
	}

	/**
	 * The {@link android.content.Context} that will be used to check network connectivity.
	 */
	//private Context mContext;

	private Environment mEnvironment;

	/**
	 * Creates a new instance of CitymapsHttpGet using the specified server name and the specified arguments.
	 *
	 * @param api     The {@link Api} in which this request will be executed.
	 * @param args    The arguments that will be used to get the URL string and HTTP parameter object.
	 */
	protected CitymapsHttpGet(Environment environment, Endpoint.Type endpointType, Object... args) {
		super();
		//mContext = api.getContext();

		mEnvironment = environment;

		try {
			String urlString = mEnvironment.buildUrlString(endpointType, null, args);
			setURI(URI.create(urlString));
			HttpParams params = getParams(args);
			if (params != null) {
				setParams(params);
			}
		} catch (MalformedURLException e) {
			throw new CitymapsRuntimeException(e.getMessage(), e);
		}
	}

	/**
	 * Get the {@link android.content.Context} the HTTP request is running in. The context will be
	 * used to check network connectivity.
	 *
	 * @return The HTTP request's context.
	 */
	/*
	public Context getContext() {
		return mContext;
	}
	*/

	public Environment getEnvironment() {
		return mEnvironment;
	}

	/**
	 * Executes this Citymaps HTTP request.
	 *
	 * @return The data returned by the {@link ResponseHandler} associated with this Citymaps HTTP request, which
	 * is itself returned by {@link #getResponseHandler()}.
	 */
	public Wrapper<D, Exception> execute() {
		if (LogEx.isLoggable(LogEx.VERBOSE)) {
			String urlString = getURI().toString();
			HttpParams params = getParams();
			LogEx.v(String.format("urlString=%s, params=%s", urlString,
					ToStringBuilder.reflectionToString(params)));
		}

		ConnectivityManager connectivityManager = (ConnectivityManager) mEnvironment.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
		if (networkInfo == null || !networkInfo.isConnectedOrConnecting())
			return new ThrowableWrapper<D, Exception>(new CitymapsConnectivityException("No network connection available"));

		Wrapper<D, Exception> response;
		AndroidHttpClient client = AndroidHttpClient.newInstance(HTTP_AGENT);
		try {
			response = client.execute(this, getResponseHandler());
		} catch (IOException e) {
			response = new ThrowableWrapper<D, Exception>(e);
		} finally {
			client.close();
		}
		return response;
	}

	/**
	 * Returns the {@link org.apache.http.client.ResponseHandler} that will be used to execute
	 * this Citymaps HTTP request.
	 *
	 * @return The ResponseHandler that will be used to execute the request.
	 */
	protected abstract ResponseHandler<Wrapper<D, Exception>> getResponseHandler();
}
