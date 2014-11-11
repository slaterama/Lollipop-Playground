package com.citymaps.mobile.android.model.request;

import android.content.Context;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.citymaps.mobile.android.app.SessionManager;
import com.citymaps.mobile.android.config.Endpoint;
import com.citymaps.mobile.android.model.GsonRequest;
import com.citymaps.mobile.android.model.vo.Config;
import com.citymaps.mobile.android.model.vo.Version;

public class MiscRequests {

	public static ConfigRequest newGetConfigRequest(Context context,
													   Response.Listener<Config> listener,
													   Response.ErrorListener errorListener) {
		String urlString = SessionManager.getInstance(context).getEnvironment().buildUrlString(Endpoint.Type.CONFIG);
		return new ConfigRequest(Method.GET, urlString, Config.class, listener, errorListener);
	}

	public static VersionRequest newGetVersionRequest(Context context,
														 Response.Listener<Version> listener,
														 Response.ErrorListener errorListener) {
		String urlString = SessionManager.getInstance(context).getEnvironment().buildUrlString(Endpoint.Type.VERSION);
		return new VersionRequest(Method.GET, urlString, Version.class, listener, errorListener);
	}

	public static class ConfigRequest extends GsonRequest<Config> {
		private ConfigRequest(int method, String url, Class<Config> clazz,
							 Response.Listener<Config> listener, Response.ErrorListener errorListener) {
			super(method, url, clazz, listener, errorListener);
		}
	}

	public static class VersionRequest extends GsonRequest<Version> {
		private VersionRequest(int method, String url, Class<Version> clazz,
							  Response.Listener<Version> listener, Response.ErrorListener errorListener) {
			super(method, url, clazz, listener, errorListener);
		}
	}

	private MiscRequests() {
	}
}
