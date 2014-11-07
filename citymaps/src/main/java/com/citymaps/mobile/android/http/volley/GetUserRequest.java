package com.citymaps.mobile.android.http.volley;

import android.content.Context;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.citymaps.mobile.android.app.SessionManager;
import com.citymaps.mobile.android.config.Endpoint;
import com.citymaps.mobile.android.model.vo.User;
import com.citymaps.mobile.android.model.vo.Version;
import com.google.gson.annotations.SerializedName;

public class GetUserRequest extends GetGsonRequest<User> {

	public GetUserRequest(Context context, User currentUser, String userId,
						  Response.Listener<User> listener, Response.ErrorListener errorListener) {
		super(SessionManager.getEnvironment(context).buildUrlString(Endpoint.Type.USER, currentUser, userId),
				User.class, null, listener, errorListener);
	}

	@Override
	protected Response<User> parseNetworkResponse(NetworkResponse response) {
		Response<WrappedUser> intermediateResponse = parseNetworkResponse(response, WrappedUser.class);
		return Response.success(intermediateResponse.result.mUser, HttpHeaderParser.parseCacheHeaders(response));
	}

	public static class WrappedUser extends Version {

		@SerializedName("user")
		private User mUser;

		public WrappedUser() {
			super();
		}
	}
}
