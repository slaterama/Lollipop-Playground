package com.citymaps.mobile.android.notused_http.request;

import com.citymaps.mobile.android.config.Environment;
import com.citymaps.mobile.android.model.vo.User;
import org.apache.http.params.HttpParams;

import java.net.MalformedURLException;

/**
 * Basic interface for a Citymaps HttpRequest object.
 */
public interface CitymapsHttpRequest {

	// TODO Add execute call here? (And not just CitymapsHttpGet)

	/**
	 * String constant used to instantiate the {@link android.net.http.AndroidHttpClient} used
	 * to make the Citymaps HTTP request.
	 */
	public static final String HTTP_AGENT = "http.agent";

	/**
	 * Returns the url string that will be used to execute this http request.
	 * @param environment The {@link Environment} used to create this CitymapsHttpRequest.
	 * @param user The user that is currently logged in to the system, or null if no user is currently logged in.
	 * @param args The arguments that will be used to get the url string and http parameter object.
	 * @return The url string.
	 */
	public String getUrlString(Environment environment, User user, Object... args) throws MalformedURLException;

	/**
	 * Get the collection of parameters (if any) that will be sent along with the CitymapsHttpRequest.
	 *
	 * @param args The arguments that will be used to determine the parameters that should be
	 *             passed to the http request.
	 * @return The collection of parameters that should be sent along with the http request.
	 */
	public HttpParams getParams(Object... args);
}
