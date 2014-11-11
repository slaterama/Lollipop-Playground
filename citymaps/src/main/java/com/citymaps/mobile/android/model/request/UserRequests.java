package com.citymaps.mobile.android.model.request;

import android.content.Context;
import android.text.TextUtils;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.citymaps.mobile.android.app.SessionManager;
import com.citymaps.mobile.android.config.Endpoint;
import com.citymaps.mobile.android.model.GsonRequest;
import com.citymaps.mobile.android.model.ResultWrapperV2;
import com.citymaps.mobile.android.model.vo.User;
import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.Map;

public class UserRequests {

	public static class UserWrapper extends ResultWrapperV2 {
		@SerializedName("user")
		private User mUser;
	}

	protected abstract static class BaseRequest extends GsonRequest<User> {

		public BaseRequest(int method, String url, Class<User> clazz, Map<String, String> headers,
						   Response.Listener<User> listener, Response.ErrorListener errorListener) {
			super(method, url, clazz, headers, listener, errorListener);
		}

		@Override
		protected Response<User> parseNetworkResponse(NetworkResponse response) {
			Response<UserWrapper> parsedResponse = parseNetworkResponse(response, UserWrapper.class);
			return Response.success(parsedResponse.result.mUser, HttpHeaderParser.parseCacheHeaders(response));
		}
	}

	public static class GetRequest extends BaseRequest {

		public GetRequest(Context context, User currentUser, String userId,
						  Response.Listener<User> listener, Response.ErrorListener errorListener) {
			super(Method.GET, SessionManager.getInstance(context).getEnvironment().buildUrlString(Endpoint.Type.USER, currentUser, userId),
					User.class, null, listener, errorListener);
		}
	}

	public static class LoginRequest extends BaseRequest {

		public static LoginRequest newInstance(Context context, String citymapsToken,
											   Response.Listener<User> listener, Response.ErrorListener errorListener) {
			return newInstance(context, null, citymapsToken, null, null, null, null, null, listener, errorListener);
		}

		public static LoginRequest newInstance(Context context, String username, String password,
											   Response.Listener<User> listener, Response.ErrorListener errorListener) {
			return newInstance(context, null, null, username, password, null, null, null, listener, errorListener);
		}

		public static LoginRequest newInstance(Context context, String userId, String citymapsToken,
											   String username, String password,
											   String thirdPartyName, String thirdPartyId, String thirdPartyToken,
											   Response.Listener<User> listener, Response.ErrorListener errorListener) {
			String urlString = SessionManager.getInstance(context).getEnvironment().buildUrlString(Endpoint.Type.USER_LOGIN);
			Map<String, String> headers = new HashMap<String, String>(8);
			if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password)) {

			}
			return null;
		}

		public LoginRequest(int method, String url, Class<User> clazz, Map<String, String> headers,
							Response.Listener<User> listener, Response.ErrorListener errorListener) {
			super(method, url, clazz, headers, listener, errorListener);
		}
	}

	private UserRequests() {
	}
}
