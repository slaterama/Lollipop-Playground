package com.citymaps.mobile.android.http.request;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.http.AndroidHttpClient;
import com.citymaps.mobile.android.app.*;
import com.citymaps.mobile.android.config.Environment;
import com.citymaps.mobile.android.model.vo.User;
import com.citymaps.mobile.android.util.LogEx;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.params.HttpParams;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;

/**
 * The base Citymaps HttpGet class used to execute Citymaps http requests.
 *
 * @param <D> The data type of the response returned by executing the request.
 */
public abstract class CitymapsHttpGet<D> extends HttpGet
		implements CitymapsHttpRequest {

	/**
	 * Get the collection of parameters (if any) that will be sent along with the Citymaps http request.
	 *
	 * @param args The arguments that will be used to determine the parameters that should be
	 *             passed to the http request.
	 * @return The collection of parameters that should be sent along with the http request.
	 */
	@Override
	public HttpParams getParams(Object... args) {
		return null;
	}

	/**
	 * Executes this Citymaps HTTP request.
	 *
	 * @param environment The {@link Environment} to use to build the HTTP request.
	 * @param user The current user, or null if no user is currently logged in.
	 * @param args The arguments that will be used to build the HTTP request.
	 * @return The data returned by the {@link ResponseHandler} associated with this Citymaps HTTP request, which
	 * is itself returned by {@link #getResponseHandler()}.
	 */
	public Wrapper<D> execute(Environment environment, User user, Object... args) {
		try {
			setURI(URI.create(getUrlString(environment, user, args)));
		} catch (MalformedURLException e) {
			throw new CitymapsRuntimeException(e);
		}

		HttpParams params = getParams(args);
		if (params != null) {
			setParams(params);
		}

		if (LogEx.isLoggable(LogEx.VERBOSE)) {
			LogEx.v(String.format("urlString=%s, params=%s", getURI(), ToStringBuilder.reflectionToString(getParams())));
		}

		Context context = environment.getContext();
		ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
		if (networkInfo == null || !networkInfo.isConnectedOrConnecting())
			return new CitymapsExceptionWrapper<D>(new CitymapsConnectivityException("No network connection available"));

		Wrapper<D> response;
		AndroidHttpClient client = AndroidHttpClient.newInstance(HTTP_AGENT);
		try {
			response = client.execute(this, getResponseHandler());
		} catch (IOException e) {
			response = new CitymapsExceptionWrapper<D>(new CitymapsException(e));
		} finally {
			client.close();
		}
		return response;
	}

	/**
	 * Returns the {@link org.apache.http.client.ResponseHandler} that will be used to execute
	 * this Citymaps http request.
	 *
	 * @return The ResponseHandler that will be used to execute the request.
	 */
	protected abstract ResponseHandler<Wrapper<D>> getResponseHandler();
}
