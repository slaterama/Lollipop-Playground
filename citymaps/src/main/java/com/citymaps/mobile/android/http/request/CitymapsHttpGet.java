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

	private Environment mEnvironment;

	/**
	 * Creates a new instance of CitymapsHttpGet using the specified {@link Environment} and arguments.
	 *
	 * @param environment The environment that will be used to execute the request.
	 * @param user The user that is currently logged in to the system, or null if no user is currently logged in.
	 * @param args The arguments that will be used to get the URL string and http parameter object.
	 */
	public CitymapsHttpGet(Environment environment, User user, Object... args) {
		super();
		mEnvironment = environment;
		try {
			setURI(URI.create(getUrlString(environment, user, args)));
			HttpParams params = getParams(args);
			if (params != null) {
				setParams(params);
			}
		} catch (MalformedURLException e) {
			throw new CitymapsRuntimeException(e);
		}
	}

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
	 * Get the {@link Environment} used to create this CitymapsHttpGet instance. The context will be
	 * used to check network connectivity.
	 *
	 * @return The Environment.
	 */
	public Environment getEnvironment() {
		return mEnvironment;
	}

	/**
	 * Executes this Citymaps http request.
	 *
	 * @return The data returned by the {@link ResponseHandler} associated with this Citymaps http request, which
	 * is itself returned by {@link #getResponseHandler()}.
	 */
	public Wrapper<D> execute() {
		if (LogEx.isLoggable(LogEx.VERBOSE)) {
			LogEx.v(String.format("urlString=%s, params=%s", getURI(), ToStringBuilder.reflectionToString(getParams())));
		}

		Context context = mEnvironment.getContext();
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
