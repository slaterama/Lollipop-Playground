package com.citymaps.mobile.android.http.request;

import android.content.Context;
import com.citymaps.mobile.android.app.Wrapper;
import com.citymaps.mobile.android.http.response.BuildResponseHandler;
import com.citymaps.mobile.android.model.vo.ApiBuild;
import org.apache.http.client.ResponseHandler;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * A CitymapsHttpGet class that returns the build of the API currently being used.
 */
public class GetBuildHttpRequest extends CitymapsHttpGet<ApiBuild> {

	/**
	 * Returns a GetBuildHttpRequest instance appropriate to the specified server.
	 *
	 * @param context       The context to use.
	 * @param serverName    The server in which this request will be executed.
	 * @param userId        The Citymaps User ID that will be used to get the build. Note that
	 *                      this argument is only needed when requesting the build in the Production
	 *                      environment.
	 * @param citymapsToken The Citymaps token that will be used to get the build. Note that
	 *                      this argument is only needed when requesting the build in the
	 *                      Production environment.
	 * @return A GetBuildHttpRequest
	 */
	public static GetBuildHttpRequest makeRequest(Context context, String serverName, String userId, String citymapsToken) {
		String encodedUserId = encode(userId);
		String encodedCitymapsToken = encode(citymapsToken);
		return new GetBuildHttpRequest(context, serverName, encodedUserId, encodedCitymapsToken);
	}

	/**
	 * The server in which this request will be executed.
	 */
	protected String mServerName;

	/**
	 * Creates a new GetBuildHttpRequest using the specified server and arguments. Note that this is
	 * a private constructor; build requests should be created using
	 * {@link #makeRequest(Context, String, String, String)}.
	 *
	 * @param context       The context to use.
	 * @param serverName    The server in which this request will be executed.
	 * @param userId        The Citymaps User ID that will be used to get the build. Note that
	 *                      this argument is only needed when requesting the build in the Production
	 *                      environment.
	 * @param citymapsToken The Citymaps token that will be used to get the build. Note that
	 *                      this argument is only needed when requesting the build in the
	 *                      Production environment.
	 */
	private GetBuildHttpRequest(Context context, String serverName, String userId, String citymapsToken) {
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
	public String getUrlString(String serverName, Object... args)
			throws MalformedURLException {
		mServerName = serverName;
		String protocol = "http"; // TODO server.getDefaultProtocol();
		String host = "coreapi.citymaps.com"; // TODO server.getHost(Server.HostType.API);
		String endpoint = "v2/status/version"; // TODO server.getApi().getEndpoint(EndpointType.BUILD, args[0]);
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
	protected ResponseHandler<Wrapper<ApiBuild, Exception>> getResponseHandler() {
		return new BuildResponseHandler();
	}
}
