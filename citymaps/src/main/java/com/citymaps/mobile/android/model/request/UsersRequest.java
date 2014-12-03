package com.citymaps.mobile.android.model.request;

import android.content.Context;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.citymaps.citymapsengine.LonLat;
import com.citymaps.mobile.android.app.SessionManager;
import com.citymaps.mobile.android.config.Api;
import com.citymaps.mobile.android.config.Endpoint;
import com.citymaps.mobile.android.config.Environment;
import com.citymaps.mobile.android.model.User;
//import com.citymaps.mobile.android.util.GsonUtils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

import java.util.Map;

public class UsersRequest extends CitymapsGsonRequest<User[]> {

	public static UsersRequest newFeaturedMappersRequest(Context context, LonLat location, float radius, int offset, int limit,
															  Response.Listener<User[]> listener, Response.ErrorListener errorListener) {
		Environment environment = SessionManager.getInstance(context).getEnvironment();
		String urlString = environment.buildUrlString(Endpoint.Type.EXPLORE_FEATURED_MAPPERS,
				location.longitude, location.latitude, radius, offset, limit);
		return new UsersRequest(Method.GET, urlString, null, null, listener, errorListener);
	}

	public UsersRequest(Api.Version version, int method, String url,
						Map<String, String> headers, Map<String, String> params,
						Response.Listener<User[]> listener, Response.ErrorListener errorListener) {
		super(version, method, url, User[].class, headers, params, listener, errorListener);
	}

	public UsersRequest(int method, String url,
						Map<String, String> headers, Map<String, String> params,
						Response.Listener<User[]> listener, Response.ErrorListener errorListener) {
		super(Api.Version.V2, method, url, User[].class, headers, params, listener, errorListener);
	}

	@Override
	protected Response<User[]> processParsedNetworkResponse(NetworkResponse response, JsonObject jsonObject) {
		FriendsWrapper result = getGson().fromJson(jsonObject, FriendsWrapper.class);
		FriendsWrapper.Suggestion suggestion = result.getData();
		User[] items = (suggestion == null ? null : suggestion.getFriends());
		return Response.success(items, HttpHeaderParser.parseCacheHeaders(response));
	}

	public static class FriendsWrapper extends ResultSuccess<FriendsWrapper.Suggestion> {
		@SerializedName("suggestion")
		private Suggestion mSuggestion;

		@Override
		public Suggestion getData() {
			return mSuggestion;
		}

		public static class Suggestion {
			@SerializedName("user_id")
			private String mUserId;

			@SerializedName("friends")
			private User[] mFriends;

			public String getUserId() {
				return mUserId;
			}

			public User[] getFriends() {
				return mFriends;
			}
		}
	}
}
