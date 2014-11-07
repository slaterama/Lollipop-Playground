package com.citymaps.mobile.android.http.volley;

import android.content.Context;
import com.android.volley.Response;
import com.citymaps.mobile.android.app.SessionManager;
import com.citymaps.mobile.android.config.Endpoint;
import com.citymaps.mobile.android.model.vo.Version;

public class GetVersionRequest extends GetGsonRequest<Version> {

	public GetVersionRequest(Context context, Response.Listener<Version> listener, Response.ErrorListener errorListener) {
		super(SessionManager.getEnvironment(context).buildUrlString(Endpoint.Type.STATUS),
				Version.class, null, listener, errorListener);
	}
}
