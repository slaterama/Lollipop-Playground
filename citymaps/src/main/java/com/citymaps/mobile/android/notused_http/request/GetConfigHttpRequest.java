package com.citymaps.mobile.android.notused_http.request;

import com.citymaps.mobile.android.app.DataWrapper;
import com.citymaps.mobile.android.app.Wrapper;
import com.citymaps.mobile.android.config.Endpoint;
import com.citymaps.mobile.android.config.Environment;
import com.citymaps.mobile.android.notused_http.response.CitymapsResponseHandler;
import com.citymaps.mobile.android.model.vo.Config;
import com.citymaps.mobile.android.model.vo.User;
import com.google.gson.JsonElement;
import org.apache.http.client.ResponseHandler;

import java.net.MalformedURLException;

/**
 * A CitymapsHttpGet class that returns Citymaps config information.
 */
public class GetConfigHttpRequest extends CitymapsHttpGet<Config> {

	/**
	 * A response handler for handling app config HTTP requests.
	 */
	private ConfigReponseHandler mResponseHandler;

	/**
	 * Executes this app config HTTPGet request.
	 *
	 * @param environment The {@link Environment} to use to build the HTTP request.
	 * @return The data returned by the {@link ResponseHandler} associated with this request, which
	 * is itself returned by {@link #getResponseHandler()}.
	 */
	public Wrapper<Config> execute(Environment environment) {
		return super.execute(environment, null);
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public String getUrlString(Environment environment, User user, Object... args) throws MalformedURLException {
		return environment.buildUrlString(Endpoint.Type.CONFIG);
	}

	/**
	 * @inheritDoc
	 */
	@Override
	protected ResponseHandler<Wrapper<Config>> getResponseHandler() {
		if (mResponseHandler == null) {
			mResponseHandler = new ConfigReponseHandler();
		}
		return mResponseHandler;
	}

	/**
	 * Class that defines a response handler for handling app config HTTP requests.
	 */
	private static class ConfigReponseHandler extends CitymapsResponseHandler<Config> {
		@Override
		protected Wrapper<Config> wrapResult(JsonElement json) {
			Config config = getGson().fromJson(json, Config.class);
			return new DataWrapper<Config>(config);
		}
	}
}