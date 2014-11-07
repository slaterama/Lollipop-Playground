package com.citymaps.mobile.android.http.volley;

import android.content.Context;
import com.android.volley.Response;
import com.citymaps.mobile.android.app.SessionManager;
import com.citymaps.mobile.android.config.Endpoint;
import com.citymaps.mobile.android.model.vo.Config;

public class GetConfigRequest extends GetGsonRequest<Config> {

	public GetConfigRequest(Context context, Response.Listener<Config> listener, Response.ErrorListener errorListener) {
		super(SessionManager.getEnvironment(context).buildUrlString(Endpoint.Type.CONFIG),
				Config.class, null, listener, errorListener);
	}
}
