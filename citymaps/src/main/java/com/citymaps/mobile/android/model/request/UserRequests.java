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

	private static final String KEY_USERNAME = "username";
	private static final String KEY_PASSWORD = "password";

	public static class UserWrapper extends ResultWrapperV2 {
		@SerializedName("user")
		private User mUser;
	}

	protected abstract static class BaseRequest extends GsonRequest<User> {

		public BaseRequest(int method, String url, Class<User> clazz,
						   Response.Listener<User> listener, Response.ErrorListener errorListener) {
			super(method, url, clazz, listener, errorListener);
		}

		@Override
		protected Response<User> parseNetworkResponse(NetworkResponse response) {
			Response<UserWrapper> parsedResponse = parseNetworkResponse(response, UserWrapper.class);
			return Response.success(parsedResponse.result.mUser, HttpHeaderParser.parseCacheHeaders(response));
		}
	}

	public static class GetRequest extends BaseRequest {

		public GetRequest(Context context, String userId,
						  Response.Listener<User> listener, Response.ErrorListener errorListener) {
			super(Method.GET, SessionManager.getInstance(context).getEnvironment().buildUrlString(Endpoint.Type.USER, userId),
					User.class, listener, errorListener);
		}
	}

	public static class LoginRequest extends BaseRequest {

		public static LoginRequest newInstance(Context context, String citymapsToken,
											   Response.Listener<User> listener, Response.ErrorListener errorListener) {
			String urlString = SessionManager.getInstance(context).getEnvironment().buildUrlString(Endpoint.Type.USER_LOGIN_WITH_TOKEN, citymapsToken);
			return new LoginRequest(Method.POST, urlString, User.class, listener, errorListener);
		}

		public static LoginRequest newInstance(Context context, String username, String password,
											   Response.Listener<User> listener, Response.ErrorListener errorListener) {
			return newInstance(context, username, password, null, null, null, listener, errorListener);
		}

		public static LoginRequest newInstance(Context context, String username, String password,
											   String thirdPartyName, String thirdPartyId, String thirdPartyToken,
											   Response.Listener<User> listener, Response.ErrorListener errorListener) {
			String urlString = SessionManager.getInstance(context).getEnvironment().buildUrlString(Endpoint.Type.USER_LOGIN);
			LoginRequest request = new LoginRequest(Method.POST, urlString, User.class, listener, errorListener);
			Map<String, String> params = new HashMap<String, String>(8);
			if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password)) {
				params.put(KEY_USERNAME, username);
				params.put(KEY_PASSWORD, password);
			}
			request.putParams(params);
			return request;
		}

		public LoginRequest(int method, String url, Class<User> clazz,
							Response.Listener<User> listener, Response.ErrorListener errorListener) {
			super(method, url, clazz, listener, errorListener);
		}
	}

	private UserRequests() {
	}
}
