package com.citymaps.mobile.android.http.request;

import com.citymaps.mobile.android.app.Wrapper;
import com.citymaps.mobile.android.config.Api;
import com.citymaps.mobile.android.config.Endpoint;
import com.citymaps.mobile.android.confignew.Environment;
import com.citymaps.mobile.android.http.response.ConfigResponseHandler;
import com.citymaps.mobile.android.model.vo.Config;
import org.apache.http.client.ResponseHandler;
import org.apache.http.params.HttpParams;

import java.net.MalformedURLException;

public class GetConfigHttpRequest extends CitymapsHttpGet<Config> {

	/**
	 * Returns a GetConfigHttpRequest instance appropriate to the specified api.
	 *
	 * @param environment     The {@link Api} in which this request will be executed.
	 * @return A GetBuildHttpRequest
	 */
	public static GetConfigHttpRequest makeRequest(Environment environment) {
		return new GetConfigHttpRequest(environment, Endpoint.Type.CONFIG);
	}

	public GetConfigHttpRequest(Environment environment, Endpoint.Type endpointType, Object... args) {
		super(environment, endpointType, args);
	}

	@Override
	protected ResponseHandler<Wrapper<Config, Exception>> getResponseHandler() {
		return new ConfigResponseHandler();
	}

	/*
	@Override
	public String getUrlString(Api api, Object... args) throws MalformedURLException {
		return api.buildUrlString(Endpoint.Type.CONFIG, args);
	}
	*/

	@Override
	public HttpParams getParams(Object... args) {
		return null;
	}
}
