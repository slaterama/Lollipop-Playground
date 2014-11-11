package com.citymaps.mobile.android.model.request;

import android.content.Context;
import com.android.volley.Response;
import com.citymaps.mobile.android.app.SessionManager;
import com.citymaps.mobile.android.config.Endpoint;
import com.citymaps.mobile.android.model.GsonRequest;
import com.citymaps.mobile.android.model.vo.Config;
import com.citymaps.mobile.android.model.vo.Version;

public class MiscRequests {

	public static class GetConfigRequest extends GsonRequest<Config> {

		public static GetConfigRequest newInstance(Context context,
												   Response.Listener<Config> listener,
												   Response.ErrorListener errorListener) {
			return new GetConfigRequest(context, listener, errorListener);
		}

		private GetConfigRequest(Context context,
								Response.Listener<Config> listener, Response.ErrorListener errorListener) {
			super(Method.GET,
					SessionManager.getInstance(context).getEnvironment().buildUrlString(Endpoint.Type.CONFIG),
					Config.class, listener, errorListener);
		}
	}

	public static class GetVersionRequest extends GsonRequest<Version> {

		public static GetVersionRequest newInstance(Context context,
													Response.Listener<Version> listener,
													Response.ErrorListener errorListener) {
			return new GetVersionRequest(context, listener, errorListener);
		}

		private GetVersionRequest(Context context,
								  Response.Listener<Version> listener, Response.ErrorListener errorListener) {
			super(Method.GET,
					SessionManager.getInstance(context).getEnvironment().buildUrlString(Endpoint.Type.VERSION),
					Version.class, listener, errorListener);
		}
	}

	private MiscRequests() {
	}
}
