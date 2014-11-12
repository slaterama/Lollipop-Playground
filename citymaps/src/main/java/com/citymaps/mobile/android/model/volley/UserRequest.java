package com.citymaps.mobile.android.model.volley;

import android.content.Context;
import android.text.TextUtils;
import com.android.volley.Response;
import com.citymaps.mobile.android.app.SessionManager;
import com.citymaps.mobile.android.config.Endpoint;
import com.citymaps.mobile.android.config.Environment;
import com.citymaps.mobile.android.model.ThirdParty;
import com.citymaps.mobile.android.model.vo.ResultWrapperV2;
import com.citymaps.mobile.android.model.vo.User;
import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.Map;

public class UserRequest extends WrappedRequest<User, UserRequest.UserWrapper> {

	private static final String KEY_USERNAME = "username";
	private static final String KEY_PASSWORD = "password";
	private static final String KEY_FIRST_NAME = "first_name";
	private static final String KEY_LAST_NAME = "last_name";
	private static final String KEY_EMAIL = "email_address";
	private static final String KEY_THIRD_PARTY_NAME = "third_party_name";
	private static final String KEY_THIRD_PARTY_ID = "third_party_id";
	private static final String KEY_THIRD_PARTY_TOKEN = "third_party_token";

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
		if (!TextUtils.isEmpty(username)) params.put(KEY_USERNAME, username);
		if (!TextUtils.isEmpty(password)) params.put(KEY_PASSWORD, password);
		request.putParams(params);
		return request;
	}

	public static UserRequest newRegisterRequest(Context context, String username, String password,
												 String firstName, String lastName, String email,
												 ThirdParty thirdParty, String thirdPartyId, String thirdPartyToken,
												 Response.Listener<User> listener, Response.ErrorListener errorListener) {
		Environment environment = SessionManager.getInstance(context).getEnvironment();
		String urlString = environment.buildUrlString(Endpoint.Type.USER_REGISTER);
		UserRequest request = new UserRequest(Method.POST, urlString, listener, errorListener);
		Map<String, String> params = new HashMap<String, String>(8);
		if (!TextUtils.isEmpty(username)) params.put(KEY_USERNAME, username);
		if (!TextUtils.isEmpty(password)) params.put(KEY_PASSWORD, password);
		if (!TextUtils.isEmpty(firstName)) params.put(KEY_FIRST_NAME, firstName);
		if (!TextUtils.isEmpty(lastName)) params.put(KEY_LAST_NAME, lastName);
		if (!TextUtils.isEmpty(email)) params.put(KEY_EMAIL, email);
		if (thirdParty != null) params.put(KEY_THIRD_PARTY_NAME, thirdParty.toString());
		if (!TextUtils.isEmpty(thirdPartyId)) params.put(KEY_THIRD_PARTY_ID, thirdPartyId);
		if (!TextUtils.isEmpty(thirdPartyToken)) params.put(KEY_THIRD_PARTY_TOKEN, thirdPartyToken);
		request.putParams(params);
		return request;
	}

	public static UserRequest newRegisterRequest(Context context, String username, String password,
												 String firstName, String lastName, String email,
												 Response.Listener<User> listener, Response.ErrorListener errorListener) {
		return newRegisterRequest(context, username, password, firstName, lastName, email, null, null, null, listener, errorListener);
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
