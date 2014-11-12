package com.citymaps.mobile.android.model.request;

import android.content.Context;
import android.text.TextUtils;
import com.android.volley.Response;
import com.citymaps.mobile.android.app.SessionManager;
import com.citymaps.mobile.android.config.Endpoint;
import com.citymaps.mobile.android.config.Environment;
import com.citymaps.mobile.android.model.GsonWrappedRequest;
import com.citymaps.mobile.android.model.ResultWrapperV2;
import com.citymaps.mobile.android.model.vo.User;
import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.Map;

public class UserRequest extends GsonWrappedRequest<User, UserRequest.UserWrapper> {

	private static final String KEY_USERNAME = "username";
	private static final String KEY_PASSWORD = "password";

	private static UserRequest newGetRequest(Context context, String userId,
											 Response.Listener<User> listener, Response.ErrorListener errorListener) {
		Environment environment = SessionManager.getInstance(context).getEnvironment();
		String urlString = environment.buildUrlString(Endpoint.Type.USER, userId);
		return new UserRequest(Method.POST, urlString, User.class, listener, errorListener);
	}

	public static UserRequest newLoginRequest(Context context, String citymapsToken,
										   Response.Listener<User> listener, Response.ErrorListener errorListener) {
		Environment environment = SessionManager.getInstance(context).getEnvironment();
		String urlString = environment.buildUrlString(Endpoint.Type.USER_LOGIN_WITH_TOKEN, citymapsToken);
		return new UserRequest(Method.POST, urlString, User.class, listener, errorListener);
	}

	public static UserRequest newLoginRequest(Context context, String username, String password,
										   Response.Listener<User> listener, Response.ErrorListener errorListener) {
		return newLoginRequest(context, username, password, null, null, null, listener, errorListener);
	}

	public static UserRequest newLoginRequest(Context context, String username, String password,
										   String thirdPartyName, String thirdPartyId, String thirdPartyToken,
										   Response.Listener<User> listener, Response.ErrorListener errorListener) {
		Environment environment = SessionManager.getInstance(context).getEnvironment();
		String urlString = environment.buildUrlString(Endpoint.Type.USER_LOGIN);
		UserRequest request = new UserRequest(Method.POST, urlString, User.class, listener, errorListener);
		Map<String, String> params = new HashMap<String, String>(8);
		if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password)) {
			params.put(KEY_USERNAME, username);
			params.put(KEY_PASSWORD, password);
		}
		request.putParams(params);
		return request;
	}

	private UserRequest(int method, String url, Class<User> clazz,
						Response.Listener<User> listener, Response.ErrorListener errorListener) {
		super(method, url, clazz, UserWrapper.class, listener, errorListener);
	}

	/*
	@Override
	protected User getData(UserWrapper result) {
		return result.mUser;
	}
	*/

	public static class UserWrapper extends ResultWrapperV2<User> {
		@SerializedName("user")
		private User mUser;

		@Override
		public User getResult() {
			return mUser;
		}
	}
}
