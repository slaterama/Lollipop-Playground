package com.citymaps.mobile.android.http.request;

import com.citymaps.mobile.android.app.Wrapper;
import com.citymaps.mobile.android.config.Api;
import com.citymaps.mobile.android.config.Endpoint;
import com.citymaps.mobile.android.confignew.Environment;
import com.citymaps.mobile.android.http.response.StatusResponseHandler;
import com.citymaps.mobile.android.model.vo.ApiStatus;
import org.apache.http.client.ResponseHandler;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;

import java.net.MalformedURLException;

/**
 * A CitymapsHttpGet class that returns the build of the API currently being used.
 */
public class GetStatusHttpRequest extends CitymapsHttpGet<ApiStatus> {

	/**
	 * Returns a GetBuildHttpRequest instance appropriate to the specified server.
	 *
	 * @param api     The {@link Api} in which this request will be executed.
	 * @return A GetBuildHttpRequest
	 */
	public static GetStatusHttpRequest makeRequest(Environment environment) {
		return new GetStatusHttpRequest(environment);
	}

	/**
	 * Creates a new GetBuildHttpRequest using the specified server and arguments. Note that this is
	 * a private constructor; build requests should be created using
	 * {@link #makeRequest(Environment)}.
	 *
	 * @param api     The {@link Api} in which this request will be executed.
	 */
	private GetStatusHttpRequest(Environment environment) {
		super(environment, Endpoint.Type.STATUS);
	}

	/**
	 * Gets the URL string that will be used to execute this request.
	 *
	 * @param api  The {@link Api} in which this request will be executed.
	 * @param args Any arguments that will be passed to the Api endpoint in order to create
	 *             the URL String.
	 * @return The URL string that will be used to execute this request.
	 * @throws MalformedURLException
	 */
	/*
	@Override
	public String getUrlString(Api api, Object... args) throws MalformedURLException {
		return api.buildUrlString(Endpoint.Type.STATUS, args);
	}
	*/

	/**
	 * Gets the HttpParams that will be passed along with this request.
	 *
	 * @param args The arguments that will be used to determine the parameters that should be
	 *             passed to the HTTP request.
	 * @return The parameters that will be passed along with this request.
	 */
	@Override
	public HttpParams getParams(Object... args) {
		HttpParams params = new BasicHttpParams();
//		params.setParameter(PARAM_NAME_CITYMAPS_TOKEN, args[1]);
		return params;
	}

	/**
	 * Gets the {@link org.apache.http.client.ResponseHandler} that will be used to process
	 * this request.
	 *
	 * @return The handler that will be used to process this request.
	 */
	@Override
	protected ResponseHandler<Wrapper<ApiStatus, Exception>> getResponseHandler() {
		return new StatusResponseHandler();
	}
}
