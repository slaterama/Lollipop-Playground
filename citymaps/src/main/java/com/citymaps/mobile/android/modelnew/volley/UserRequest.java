package com.citymaps.mobile.android.modelnew.volley;

import android.content.Context;
import android.text.TextUtils;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.citymaps.mobile.android.app.SessionManager;
import com.citymaps.mobile.android.config.Api;
import com.citymaps.mobile.android.config.Endpoint;
import com.citymaps.mobile.android.config.Environment;
import com.citymaps.mobile.android.modelnew.User;
import com.citymaps.mobile.android.modelnew.ThirdParty;
import com.citymaps.mobile.android.util.GsonUtils;
import com.citymaps.mobile.android.util.LogEx;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.Map;

public class UserRequest extends CitymapsGsonRequest<User> {

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
		return new UserRequest(Method.POST, urlString, null, null, listener, errorListener);
	}

	public static UserRequest newLoginRequest(Context context, String citymapsToken,
											  Response.Listener<User> listener, Response.ErrorListener errorListener) {
		Environment environment = SessionManager.getInstance(context).getEnvironment();
		String urlString = environment.buildUrlString(Endpoint.Type.USER_LOGIN_WITH_TOKEN, citymapsToken);
		return new UserRequest(Method.POST, urlString, null, null, listener, errorListener);
	}

	public static UserRequest newLoginRequest(Context context, String username, String password,
											  Response.Listener<User> listener, Response.ErrorListener errorListener) {
		return newLoginRequest(context, username, password, null, null, null, listener, errorListener);
	}

	public static UserRequest newLoginRequest(Context context, String username, String password,
											  ThirdParty thirdParty, String thirdPartyId, String thirdPartyToken,
											  Response.Listener<User> listener, Response.ErrorListener errorListener) {
		Environment environment = SessionManager.getInstance(context).getEnvironment();
		String urlString = environment.buildUrlString(Endpoint.Type.USER_LOGIN);
		Map<String, String> params = new HashMap<String, String>(8);
		if (!TextUtils.isEmpty(username)) params.put(KEY_USERNAME, username);
		if (!TextUtils.isEmpty(password)) params.put(KEY_PASSWORD, password);
		if (thirdParty != null) params.put(KEY_THIRD_PARTY_NAME, thirdParty.toString());
		if (!TextUtils.isEmpty(thirdPartyId)) params.put(KEY_THIRD_PARTY_ID, thirdPartyId);
		if (!TextUtils.isEmpty(thirdPartyToken)) params.put(KEY_THIRD_PARTY_TOKEN, thirdPartyToken);
		return new UserRequest(Method.POST, urlString, null, params, listener, errorListener);
	}

	public static UserRequest newRegisterRequest(Context context, String username, String password,
												 String firstName, String lastName, String email,
												 Response.Listener<User> listener, Response.ErrorListener errorListener) {
		return newRegisterRequest(context, username, password, firstName, lastName, email,
				null, null, null, listener, errorListener);
	}

	public static UserRequest newRegisterRequest(Context context, String username, String password,
												 String firstName, String lastName, String email,
												 ThirdParty thirdParty, String thirdPartyId, String thirdPartyToken,
												 Response.Listener<User> listener, Response.ErrorListener errorListener) {
		Environment environment = SessionManager.getInstance(context).getEnvironment();
		String urlString = environment.buildUrlString(Endpoint.Type.USER_REGISTER);
		Map<String, String> params = new HashMap<String, String>(8);
		if (!TextUtils.isEmpty(username)) params.put(KEY_USERNAME, username);
		if (!TextUtils.isEmpty(password)) params.put(KEY_PASSWORD, password);
		if (!TextUtils.isEmpty(firstName)) params.put(KEY_FIRST_NAME, firstName);
		if (!TextUtils.isEmpty(lastName)) params.put(KEY_LAST_NAME, lastName);
		if (!TextUtils.isEmpty(email)) params.put(KEY_EMAIL, email);
		if (thirdParty != null) params.put(KEY_THIRD_PARTY_NAME, thirdParty.toString());
		if (!TextUtils.isEmpty(thirdPartyId)) params.put(KEY_THIRD_PARTY_ID, thirdPartyId);
		if (!TextUtils.isEmpty(thirdPartyToken)) params.put(KEY_THIRD_PARTY_TOKEN, thirdPartyToken);
		return new UserRequest(Method.POST, urlString, null, params, listener, errorListener);
	}

	public UserRequest(Api.Version version, int method, String url,
					   Map<String, String> headers, Map<String, String> params,
					   Response.Listener<User> listener, Response.ErrorListener errorListener) {
		super(version, method, url, User.class, headers, params, listener, errorListener);
	}

	public UserRequest(int method, String url,
					   Map<String, String> headers, Map<String, String> params,
					   Response.Listener<User> listener, Response.ErrorListener errorListener) {
		super(Api.Version.V2, method, url, User.class, headers, params, listener, errorListener);
	}

	@Override
	protected Response<User> processParsedNetworkResponse(NetworkResponse response, JsonObject jsonObject) {
		LogEx.d(String.format("mVersion=%s", mVersion));
		Gson gson = GsonUtils.getGson();
		switch (mVersion) {
			case V1:
				// V1 api calls return "successful" responses with a code != 0 if there was an error
				int code = jsonObject.get("code").getAsInt();
				if (code == 0) {
					// Parse into a success result
					UserWrapperV1 result = gson.fromJson(jsonObject, UserWrapperV1.class);
					User user = result.getData();
					return Response.success(user, HttpHeaderParser.parseCacheHeaders(response));
				} else {
					// Parse into an error result
					ResultErrorV1 result = gson.fromJson(jsonObject, ResultErrorV1.class);
					String error = result.getError();
					return Response.error(new VolleyError(error, new ServerError(response)));
				}
			case V2:
			default:
				UserWrapper result = gson.fromJson(jsonObject, UserWrapper.class);
				User user = result.getData();
				return Response.success(user, HttpHeaderParser.parseCacheHeaders(response));
		}
	}

	public static class UserWrapper extends ResultSuccess<User> {
		@SerializedName("user")
		private User mUser;

		@Override
		public User getData() {
			return mUser;
		}
	}

	public static class UserWrapperV1 extends ResultSuccessV1<User> {
		@SerializedName("user")
		private User mUser;

		@Override
		public User getData() {
			return mUser;
		}
	}
}
