package com.citymaps.mobile.android.model.volley;

import android.content.Context;
import com.android.volley.Response;
import com.citymaps.mobile.android.app.SessionManager;
import com.citymaps.mobile.android.config.Endpoint;
import com.citymaps.mobile.android.model.vo.Config;

public class ConfigRequest extends GsonRequest<Config> {

	public static ConfigRequest newGetRequest(Context context,
											  Response.Listener<Config> listener,
											  Response.ErrorListener errorListener) {
		String urlString = SessionManager.getInstance(context).getEnvironment().buildUrlString(Endpoint.Type.CONFIG);
		return new ConfigRequest(Method.GET, urlString, Config.class, listener, errorListener);
	}

	public ConfigRequest(int method, String url, Class<Config> clazz,
						 Response.Listener<Config> listener, Response.ErrorListener errorListener) {
		super(method, url, clazz, listener, errorListener);
	}
}
