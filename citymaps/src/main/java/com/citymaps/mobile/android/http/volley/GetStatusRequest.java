package com.citymaps.mobile.android.http.volley;

import android.content.Context;
import com.android.volley.Response;
import com.citymaps.mobile.android.app.SessionManager;
import com.citymaps.mobile.android.config.Endpoint;
import com.citymaps.mobile.android.model.vo.Status;

public class GetStatusRequest extends GetGsonRequest<Status> {

	public GetStatusRequest(Context context, Response.Listener<Status> listener, Response.ErrorListener errorListener) {
		super(SessionManager.getEnvironment(context).buildUrlString(Endpoint.Type.STATUS),
				Status.class, null, listener, errorListener);
	}
}
