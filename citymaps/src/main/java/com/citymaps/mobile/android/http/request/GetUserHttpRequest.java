package com.citymaps.mobile.android.http.request;

import android.content.Context;
import com.citymaps.mobile.android.app.Wrapper;
import com.citymaps.mobile.android.model.vo.User;
import org.apache.http.client.ResponseHandler;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * A CitymapsHttpGet class that returns a user of the Citymaps app.
 */
public class GetUserHttpRequest extends CitymapsHttpGet<User> {

	/**
	 * Returns a GetUserHttpRequest instance appropriate to the specified server.
	 *
	 * @param context       The context to use.
	 * @param serverName    The server in which this request will be executed.
	 * @param userId        The Citymaps User ID that will be used to get the user.
	 * @param citymapsToken The Citymaps token that will be used to get the user.
	 * @return A {@link Wrapper} object that will wrap either the
	 * user if the request succeeded, or any Exception that may have been encountered along the way.
	 */
	public static GetUserHttpRequest makeRequest(Context context, String serverName, String userId, String citymapsToken) {
		String encodedUserId = encode(userId);
		String encodedCitymapsToken = encode(citymapsToken);
		return new GetUserHttpRequest(context, serverName, encodedUserId, encodedCitymapsToken);
	}

	/**
	 * Creates a new GetUserHttpRequest using the specified server and arguments. Note that this is
	 * a private constructor; build requests should be created using
	 * {@link #makeRequest(Context, String, String, String)}.
	 *
	 * @param context       The context to use.
	 * @param serverName    The server in which this request will be executed.
	 * @param userId        The Citymaps User ID that will be used to get the user.
	 * @param citymapsToken The Citymaps token that will be used to get the user.
	 */
	private GetUserHttpRequest(Context context, String serverName, String userId, String citymapsToken) {
		super(context, serverName, userId, citymapsToken);
	}

	/**
	 * Gets the URL string that will be used to execute this request.
	 *
	 * @param serverName The name of the server that will be used to
	 *                   determine the host and endpoint of the HTTP request.
	 * @param args       Any arguments that will be passed to the Api endpoint in order to create
	 *                   the URL String.
	 * @return The URL string that will be used to execute this request.
	 * @throws MalformedURLException
	 */
	@Override
	public String getUrlString(String serverName, Object... args) throws MalformedURLException {
		String protocol = ""; // TODO server.getDefaultProtocol();
		String host = ""; // TODO server.getHost(Server.HostType.API);
		String endpoint = ""; // TODO server.getApi().getEndpoint(EndpointType.USER, args[0]);
		return new URL(protocol, host, endpoint).toString();
	}

	/**
	 * Gets the HttpParams that will be passed along with this request.
	 *
	 * @param serverName The name of the server that will (optionally) be used to
	 *                   determine the parameters that should be passed to the HTTP request. Note that
	 *                   as of now this argument is rarely, if ever, used.
	 * @param args       The arguments that will be used to determine the parameters that should be
	 *                   passed to the HTTP request.
	 * @return The parameters that will be passed along with this request.
	 */
	@Override
	public HttpParams getParams(String serverName, Object... args) {
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
		return null; // TODO new UserResponseHandler();
	}
}
