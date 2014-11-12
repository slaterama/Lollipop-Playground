package com.citymaps.mobile.android.model.request;

import android.content.Context;
import android.text.TextUtils;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.citymaps.mobile.android.app.SessionManager;
import com.citymaps.mobile.android.config.Endpoint;
import com.citymaps.mobile.android.config.Environment;
import com.citymaps.mobile.android.model.WrappedRequest;
import com.citymaps.mobile.android.model.ResultWrapperV2;
import com.citymaps.mobile.android.model.vo.User;
import com.citymaps.mobile.android.util.LogEx;
import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.Map;

public class UserRequest extends WrappedRequest<User, UserRequest.UserWrapper> {

	private static final String KEY_USERNAME = "username";
	private static final String KEY_PASSWORD = "password";

	private static UserRequest newGetRequest(Context context, String userId,
											 Response.Listener<User> listener, Response.ErrorListener errorListener) {
		Environment environment = SessionManager.getInstance(context).getEnvironment();
		String urlString = environment.buildUrlString(Endpoint.Type.USER, userId);
		return new UserRequest(Method.POST, urlString, listener, errorListener);
	}

	public static UserRequest newLoginRequest(Context context, String citymapsToken,
											  Response.Listener<User> listener, Response.ErrorListener errorListener) {
		Environment environment = SessionManager.getInstance(context).getEnvironment();
		String urlString = environment.buildUrlString(Endpoint.Type.USER_LOGIN_WITH_TOKEN, citymapsToken);
		return new UserRequest(Method.POST, urlString, listener, errorListener);
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
		UserRequest request = new UserRequest(Method.POST, urlString, listener, errorListener);
		Map<String, String> params = new HashMap<String, String>(8);
		if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password)) {
			params.put(KEY_USERNAME, username);
			params.put(KEY_PASSWORD, password);
		}
		request.putParams(params);
		return request;
	}
	
	public UserRequest(int method, String url,
					   Response.Listener<User> listener, Response.ErrorListener errorListener) {
		super(method, url, User.class, UserWrapper.class, listener, errorListener);
	}

	public static class UserWrapper extends ResultWrapperV2<User> {
		@SerializedName("user")
		private User mUser;

		@Override
		public User getResult() {
			return mUser;
		}
	}
}
