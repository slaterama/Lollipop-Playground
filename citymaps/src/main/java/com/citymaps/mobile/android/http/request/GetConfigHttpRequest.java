package com.citymaps.mobile.android.http.request;

import android.content.Context;
import com.citymaps.mobile.android.app.Wrapper;
import com.citymaps.mobile.android.model.vo.Config;
import org.apache.http.client.ResponseHandler;
import org.apache.http.params.HttpParams;

import java.net.MalformedURLException;

public class GetConfigHttpRequest extends CitymapsHttpGet<Config> {

	public static GetConfigHttpRequest makeRequest(Context context, String serverName) {
		return new GetConfigHttpRequest(context, serverName);
	}

	private GetConfigHttpRequest(Context context, String serverName, Object... args) {
		super(context, serverName, args);
	}

	@Override
	protected ResponseHandler<Wrapper<Config, Exception>> getResponseHandler() {
		return null;
	}

	@Override
	public String getUrlString(String serverName, Object... args) throws MalformedURLException {
		return null;
	}

	@Override
	public HttpParams getParams(String serverName, Object... args) {
		return null;
	}
}
