package com.citymaps.mobile.android.http.request;

import org.apache.http.params.HttpParams;

import java.net.MalformedURLException;

/**
 * Basic interface for a Citymaps HttpRequest object.
 */
public interface CitymapsHttpRequest {

	/**
	 * UTF-8 string used in Citymaps HttpRequest classes for UTF-8 encoding.
	 */
	public static final String UTF_8 = "UTF-8";

	/**
	 * String constant containing the name of the the Citymaps Token HTTP parameter
	 * for Citymaps HTTP requests.
	 */
	public static final String PARAM_NAME_CITYMAPS_TOKEN = "citymaps_token";

	/**
	 * Get the URL String that will be used to execute the Citymaps HTTP request.
	 *
	 * @param serverName The name of the server that will be used to
	 *                   determine the host and endpoint of the HTTP request.
	 * @param args       Any arguments that will be passed to the Api endpoint in order to create
	 *                   the URL String.
	 * @return The URL String.
	 * @throws MalformedURLException
	 */
	public String getUrlString(String serverName, Object... args)
			throws MalformedURLException;

	/**
	 * Get the collection of parameters (if any) that will be sent along with the Citymaps HTTP request.
	 *
	 * @param serverName The name of the server that will (optionally) be used to
	 *                   determine the parameters that should be passed to the HTTP request. Note that
	 *                   as of now this argument is rarely, if ever, used.
	 * @param args       The arguments that will be used to determine the parameters that should be
	 *                   passed to the HTTP request.
	 * @return The collection of parameters that should be sent along with the HTTP request.
	 */
	public HttpParams getParams(String serverName, Object... args);
}
