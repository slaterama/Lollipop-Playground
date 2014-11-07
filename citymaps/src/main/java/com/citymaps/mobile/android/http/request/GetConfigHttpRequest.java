package com.citymaps.mobile.android.http.request;

import com.citymaps.mobile.android.app.DataWrapper;
import com.citymaps.mobile.android.app.Wrapper;
import com.citymaps.mobile.android.config.Endpoint;
import com.citymaps.mobile.android.config.Environment;
import com.citymaps.mobile.android.http.response.CitymapsResponseHandler;
import com.citymaps.mobile.android.model.vo.Config;
import com.citymaps.mobile.android.model.vo.User;
import com.google.gson.JsonElement;
import org.apache.http.client.ResponseHandler;

import java.net.MalformedURLException;

/**
 * A CitymapsHttpGet class that returns Citymaps config information.
 */
public class GetConfigHttpRequest extends CitymapsHttpGet<Config> {

	private ConfigReponseHandler mResponseHandler;

	/**
	 * Creates a new GetConfigHttpRequest using the specified {@link Environment}.
	 *
	 * @param environment The environment that will be used to execute the request.
	 */
	public GetConfigHttpRequest(Environment environment) {
		super(environment, null);
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

	private static class ConfigReponseHandler extends CitymapsResponseHandler<Config> {
		@Override
		protected Wrapper<Config> wrapResult(JsonElement json) {
			Config config = getGson().fromJson(json, Config.class);
			return new DataWrapper<Config>(config);
		}
	}
}
