package com.citymaps.mobile.android.http.request;

import com.citymaps.mobile.android.app.DataWrapper;
import com.citymaps.mobile.android.app.Wrapper;
import com.citymaps.mobile.android.config.Endpoint;
import com.citymaps.mobile.android.config.Environment;
import com.citymaps.mobile.android.http.response.CitymapsResponseHandler;
import com.citymaps.mobile.android.model.vo.ApiStatus;
import com.citymaps.mobile.android.model.vo.User;
import com.google.gson.JsonElement;
import org.apache.http.client.ResponseHandler;

import java.net.MalformedURLException;

/**
 * A CitymapsHttpGet class that returns the status (version and build) of the API currently being used.
 */
public class GetApiStatusHttpRequest extends CitymapsHttpGet<ApiStatus> {

	/**
	 * A response handler for handling API status HTTP requests.
	 */
	private ApiStatusResponseHandler mResponseHandler;

	/**
	 * Executes this API status HTTPGet request.
	 *
	 * @param environment The {@link Environment} to use to build the HTTP request.
	 * @return The data returned by the {@link ResponseHandler} associated with this request, which
	 * is itself returned by {@link #getResponseHandler()}.
	 */
	public Wrapper<ApiStatus> execute(Environment environment) {
		return super.execute(environment, null);
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public String getUrlString(Environment environment, User user, Object... args) throws MalformedURLException {
		return environment.buildUrlString(Endpoint.Type.STATUS);
	}

	/**
	 * @inheritDoc
	 */
	@Override
	protected ResponseHandler<Wrapper<ApiStatus>> getResponseHandler() {
		if (mResponseHandler == null) {
			mResponseHandler = new ApiStatusResponseHandler();
		}
		return mResponseHandler;
	}

	/**
	 * Class that defines a response handler for handling API status HTTP requests.
	 */
	private static class ApiStatusResponseHandler extends CitymapsResponseHandler<ApiStatus> {
		@Override
		protected Wrapper<ApiStatus> wrapResult(JsonElement json) {
			ApiStatus apiStatus = getGson().fromJson(json, ApiStatus.class);
			return new DataWrapper<ApiStatus>(apiStatus);
		}
	}
}
