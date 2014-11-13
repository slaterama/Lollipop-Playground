package com.citymaps.mobile.android.modelnew.volley;

import android.content.Context;
import com.android.volley.Response;
import com.citymaps.mobile.android.app.SessionManager;
import com.citymaps.mobile.android.config.Endpoint;
import com.citymaps.mobile.android.modelnew.Version;

public class VersionRequest extends GsonRequest<Version> {

	public static VersionRequest newGetRequest(Context context,
											   Response.Listener<Version> listener,
											   Response.ErrorListener errorListener) {
		String urlString = SessionManager.getInstance(context).getEnvironment().buildUrlString(Endpoint.Type.VERSION);
		return new VersionRequest(Method.GET, urlString, listener, errorListener);
	}

	public VersionRequest(int method, String url,
						  Response.Listener<Version> listener, Response.ErrorListener errorListener) {
		super(method, url, Version.class, null, null, listener, errorListener);
	}
}
