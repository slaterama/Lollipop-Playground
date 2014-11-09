package com.citymaps.mobile.android.notused_http.response;

import com.citymaps.mobile.android.app.Wrapper;
import com.citymaps.mobile.android.model.vo.User;
import com.google.gson.JsonElement;

/**
 * A ResponseHandler class designed to handle a User HTTP response.
 */
public class UserResponseHandler extends CitymapsResponseHandler<User> {

	/**
	 * Wraps user data.
	 * @param json The {@link com.google.gson.JsonElement} that contains the content resulting from the HTTP request.
	 * @return The wrapped user data.
	 */
	@Override
	protected Wrapper<User> wrapResult(JsonElement json) {
		return null;
		/*
		UserResult userResult = getGson().fromJson(json, UserResult.class);
		int code = userResult.getCode();
		if (code == 0)
			return new DataWrapper<User>(userResult.getUser());
		else {
			CitymapsHttpException e = new CitymapsHttpException(code, userResult.getError());
			return new CitymapsExceptionWrapper<User>(e);
		}
		*/
	}

	/**
	 * A convenience class that represents a user result.
	 */
//	private static class UserResult extends ApiResult {
		/**
		 * The user data contained in this result.
		 */
//		@SerializedName("user")
//		private User mUser;

		/**
		 * Gets the user data from this result.
		 * @return The user data.
		 */
//		public User getUser() {
//			return mUser;
//		}
//	}
}