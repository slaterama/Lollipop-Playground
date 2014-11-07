package com.citymaps.mobile.android.http.volley;

import android.content.Context;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.citymaps.mobile.android.app.SessionManager;
import com.citymaps.mobile.android.config.Endpoint;
import com.citymaps.mobile.android.model.vo.Status;
import com.citymaps.mobile.android.model.vo.User;
import com.citymaps.mobile.android.util.LogEx;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.gson.annotations.SerializedName;

import java.io.UnsupportedEncodingException;

public class GetUserRequest extends GetGsonRequest<User> {

	public GetUserRequest(Context context, User currentUser, String userId,
						  Response.Listener<User> listener, Response.ErrorListener errorListener) {
		super(SessionManager.getEnvironment(context).buildUrlString(Endpoint.Type.USER, currentUser, userId),
				User.class, null, listener, errorListener);
	}

	@Override
	protected Response<User> parseNetworkResponse(NetworkResponse response) {
		try {
			String json = new String(
					response.data,
					HttpHeaderParser.parseCharset(response.headers));

			if (LogEx.isLoggable(LogEx.VERBOSE)) {
				if (sJsonParser == null) {
					sJsonParser = new JsonParser();
				}
				LogEx.v(String.format("response=%s", getGson().toJson(sJsonParser.parse(json))));
			}

			UserResult result = getGson().fromJson(json, UserResult.class);
			return Response.success(result.mUser, HttpHeaderParser.parseCacheHeaders(response));

//			return Response.success(
//					getGson().fromJson(json, mClass),
//					HttpHeaderParser.parseCacheHeaders(response));
		} catch (UnsupportedEncodingException e) {
			return Response.error(new ParseError(e));
		} catch (JsonSyntaxException e) {
			return Response.error(new ParseError(e));
		}
	}

	public static class UserResult extends Status {

		@SerializedName("user")
		private User mUser;

		public UserResult() {
			super();
		}
	}
}
