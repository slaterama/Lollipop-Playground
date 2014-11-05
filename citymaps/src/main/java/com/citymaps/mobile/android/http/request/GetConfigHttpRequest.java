package com.citymaps.mobile.android.http.request;

import android.content.Context;
import com.citymaps.mobile.android.app.Wrapper;
import com.citymaps.mobile.android.config.Api;
import com.citymaps.mobile.android.config.Endpoint;
import com.citymaps.mobile.android.model.vo.Config;
import org.apache.http.client.ResponseHandler;
import org.apache.http.params.HttpParams;

import java.net.MalformedURLException;

public class GetConfigHttpRequest extends CitymapsHttpGet<Config> {

	public GetConfigHttpRequest(Context context, Api api, Object... args) {
		super(context, api, args);
	}

	@Override
	protected ResponseHandler<Wrapper<Config, Exception>> getResponseHandler() {
		return null;
	}

	@Override
	public String getUrlString(Api api, Object... args) throws MalformedURLException {
		return api.buildUrlString(Endpoint.Type.STATUS, args);
	}

	@Override
	public HttpParams getParams(Object... args) {
		return null;
	}
}
