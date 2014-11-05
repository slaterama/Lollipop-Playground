package com.citymaps.mobile.android.http.request;

import com.citymaps.mobile.android.app.Wrapper;
import com.citymaps.mobile.android.config.Api;
import com.citymaps.mobile.android.config.Endpoint;
import com.citymaps.mobile.android.http.response.ConfigResponseHandler;
import com.citymaps.mobile.android.model.vo.Config;
import org.apache.http.client.ResponseHandler;
import org.apache.http.params.HttpParams;

import java.net.MalformedURLException;

public class GetConfigHttpRequest extends CitymapsHttpGet<Config> {

	/**
	 * Returns a GetConfigHttpRequest instance appropriate to the specified api.
	 *
	 * @param api     The {@link Api} in which this request will be executed.
	 * @return A GetBuildHttpRequest
	 */
	public static GetConfigHttpRequest makeRequest(Api api) {
		return new GetConfigHttpRequest(api);
	}

	private GetConfigHttpRequest(Api api, Object... args) {
		super(api, args);
	}

	@Override
	protected ResponseHandler<Wrapper<Config, Exception>> getResponseHandler() {
		return new ConfigResponseHandler();
	}

	@Override
	public String getUrlString(Api api, Object... args) throws MalformedURLException {
		return api.buildUrlString(Endpoint.Type.CONFIG, args);
	}

	@Override
	public HttpParams getParams(Object... args) {
		return null;
	}
}
