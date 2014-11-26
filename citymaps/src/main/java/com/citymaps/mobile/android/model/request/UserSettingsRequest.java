package com.citymaps.mobile.android.model.request;

import android.content.Context;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.citymaps.mobile.android.app.SessionManager;
import com.citymaps.mobile.android.config.Api;
import com.citymaps.mobile.android.config.Endpoint;
import com.citymaps.mobile.android.config.Environment;
import com.citymaps.mobile.android.model.UserSettings;
import com.citymaps.mobile.android.util.GsonUtils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

import java.util.Map;

public class UserSettingsRequest extends CitymapsGsonRequest<UserSettings> {

	public static UserSettingsRequest newGetRequest(Context context, String userId,
												 Response.Listener<UserSettings> listener, Response.ErrorListener errorListener) {
		Environment environment = SessionManager.getInstance(context).getEnvironment();
		String urlString = environment.buildUrlString(Endpoint.Type.USER_SETTINGS, userId);
		return new UserSettingsRequest(Api.Version.V2, true, Method.GET, urlString, null, null, listener, errorListener);
	}

	public static UserSettingsRequest newUpdateRequest(Context context, String settingsId, Map<String, String> params,
													   Response.Listener<UserSettings> listener, Response.ErrorListener errorListener) {
		Environment environment = SessionManager.getInstance(context).getEnvironment();
		String urlString = environment.buildUrlString(Endpoint.Type.USER_SETTINGS, settingsId);
		return new UserSettingsRequest(Api.Version.V2, true, Method.POST, urlString, null, params, listener, errorListener);
	}

	protected boolean mResponseWrapsArray = false;

	public UserSettingsRequest(Api.Version version, boolean responseWrapsArray, int method, String url,
					   Map<String, String> headers, Map<String, String> params,
					   Response.Listener<UserSettings> listener, Response.ErrorListener errorListener) {
		super(version, method, url, UserSettings.class, headers, params, listener, errorListener);
		mResponseWrapsArray = responseWrapsArray;
	}

	public UserSettingsRequest(boolean responseWrapsArray, int method, String url,
					   Map<String, String> headers, Map<String, String> params,
					   Response.Listener<UserSettings> listener, Response.ErrorListener errorListener) {
		super(Api.Version.V2, method, url, UserSettings.class, headers, params, listener, errorListener);
		mResponseWrapsArray = responseWrapsArray;
	}

	@Override
	protected Response<UserSettings> processParsedNetworkResponse(NetworkResponse response, JsonObject jsonObject) {
		Gson gson = GsonUtils.getGson();
		switch (mVersion) {
			case V1:
				// V1 api calls return "successful" responses with a code != 0 if there was an error
				int code = jsonObject.get(MEMBER_NAME_CODE_V1).getAsInt();
				if (code == 0) {
					// Parse into a success result
					if (mResponseWrapsArray) {
						UserSettingsArrayWrapperV1 result = gson.fromJson(jsonObject, UserSettingsArrayWrapperV1.class);
						UserSettings[] userSettings = result.getData();
						if (userSettings == null || userSettings.length < 1) {
							return null;
						} else {
							return Response.success(userSettings[0], HttpHeaderParser.parseCacheHeaders(response));
						}
					} else {
						UserSettingsWrapperV1 result = gson.fromJson(jsonObject, UserSettingsWrapperV1.class);
						UserSettings userSettings = result.getData();
						return Response.success(userSettings, HttpHeaderParser.parseCacheHeaders(response));
					}
				} else {
					// Parse into an error result
					ResultErrorV1 result = gson.fromJson(jsonObject, ResultErrorV1.class);
					String error = result.getError();
					return Response.error(new VolleyError(error, new ServerError(response)));
				}
			case V2:
			default:
				if (mResponseWrapsArray) {
					UserSettingsArrayWrapper result = gson.fromJson(jsonObject, UserSettingsArrayWrapper.class);
					UserSettings[] userSettings = result.getData();
					if (userSettings == null || userSettings.length < 1) {
						return null;
					} else {
						return Response.success(userSettings[0], HttpHeaderParser.parseCacheHeaders(response));
					}
				} else {
					UserSettingsWrapper result = gson.fromJson(jsonObject, UserSettingsWrapper.class);
					UserSettings userSettings = result.getData();
					return Response.success(userSettings, HttpHeaderParser.parseCacheHeaders(response));
				}
		}
	}

	public static class UserSettingsWrapper extends ResultSuccess<UserSettings> {
		@SerializedName("user_settings")
		private UserSettings mUserSettings;

		@Override
		public UserSettings getData() {
			return mUserSettings;
		}
	}

	public static class UserSettingsArrayWrapper extends ResultSuccess<UserSettings[]> {
		@SerializedName("user_settings")
		private UserSettings[] mUserSettings;

		@Override
		public UserSettings[] getData() {
			return mUserSettings;
		}
	}

	public static class UserSettingsWrapperV1 extends ResultSuccessV1<UserSettings> {
		@SerializedName("user_settings")
		private UserSettings mUserSettings;

		@Override
		public UserSettings getData() {
			return mUserSettings;
		}
	}

	public static class UserSettingsArrayWrapperV1 extends ResultSuccessV1<UserSettings[]> {
		@SerializedName("user_settings")
		private UserSettings[] mUserSettings;

		@Override
		public UserSettings[] getData() {
			return mUserSettings;
		}
	}
}
