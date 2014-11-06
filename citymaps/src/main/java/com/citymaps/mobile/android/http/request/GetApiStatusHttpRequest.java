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

	private CitymapsResponseHandler<ApiStatus> mResponseHandler = new CitymapsResponseHandler<ApiStatus>() {
		@Override
		protected Wrapper<ApiStatus> wrapResult(JsonElement json) {
			ApiStatus apiStatus = getGson().fromJson(json, ApiStatus.class);
			return new DataWrapper<ApiStatus>(apiStatus);
		}
	};

	/**
	 * Creates a new GetStatusHttpRequest using the specified {@link Environment}.
	 *
	 * @param environment The environment that will be used to execute the request.
	 */
	public GetApiStatusHttpRequest(Environment environment) {
		super(environment, null);
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
		return mResponseHandler;
	}
}
