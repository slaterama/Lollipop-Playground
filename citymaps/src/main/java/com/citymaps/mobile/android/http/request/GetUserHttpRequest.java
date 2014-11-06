package com.citymaps.mobile.android.http.request;

import com.citymaps.mobile.android.app.Wrapper;
import com.citymaps.mobile.android.config.Api;
import com.citymaps.mobile.android.config.Endpoint;
import com.citymaps.mobile.android.confignew.Environment;
import com.citymaps.mobile.android.http.response.UserResponseHandler;
import com.citymaps.mobile.android.model.vo.User;
import org.apache.http.client.ResponseHandler;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;

import java.net.MalformedURLException;

/**
 * A CitymapsHttpGet class that returns a user of the Citymaps app.
 */
public class GetUserHttpRequest extends CitymapsHttpGet<User> {

	/**
	 * Returns a GetUserHttpRequest instance appropriate to the specified server.
	 *
	 * @param api           The {@link Api} in which this request will be executed.
	 * @param userId        The Citymaps User ID that will be used to get the user.
	 * @param citymapsToken The Citymaps token that will be used to get the user.
	 * @return A {@link Wrapper} object that will wrap either the
	 * user if the request succeeded, or any Exception that may have been encountered along the way.
	 */
	public static GetUserHttpRequest makeRequest(Environment environment, User user, String userId) {
		return new GetUserHttpRequest(environment, Endpoint.Type.USER, user, userId);
	}

	/**
	 * Creates a new GetUserHttpRequest using the specified server and arguments. Note that this is
	 * a private constructor; build requests should be created using
	 * {@link #makeRequest(Api, String, String)}.
	 *
	 * @param api           The {@link Api} in which this request will be executed.
	 * @param userId        The Citymaps User ID that will be used to get the user.
	 * @param citymapsToken The Citymaps token that will be used to get the user.
	 */
	public GetUserHttpRequest(Environment environment, Endpoint.Type endpointType, User user, String userId) {
		super(environment, endpointType, user, userId);
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
		return api.buildUrlString(Endpoint.Type.USER, args);
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
		params.setParameter(PARAM_NAME_CITYMAPS_TOKEN, args[1]);
		return params;
	}

	/**
	 * Gets the {@link org.apache.http.client.ResponseHandler} that will be used to process
	 * this request.
	 *
	 * @return The handler that will be used to process this request.
	 */
	@Override
	protected ResponseHandler<Wrapper<User, Exception>> getResponseHandler() {
		return new UserResponseHandler();
	}
}
