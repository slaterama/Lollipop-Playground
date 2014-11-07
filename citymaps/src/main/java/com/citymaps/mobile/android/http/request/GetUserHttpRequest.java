package com.citymaps.mobile.android.http.request;

import com.citymaps.mobile.android.app.Wrapper;
import com.citymaps.mobile.android.config.Endpoint;
import com.citymaps.mobile.android.config.Environment;
import com.citymaps.mobile.android.http.response.UserResponseHandler;
import com.citymaps.mobile.android.model.vo.User;
import org.apache.http.client.ResponseHandler;

import java.net.MalformedURLException;

/**
 * A CitymapsHttpGet class that returns a user of the Citymaps app.
 */
public class GetUserHttpRequest extends CitymapsHttpGet<User> {

	/**
	 * A response handler for handling app config HTTP requests.
	 */
	private UserResponseHandler mResponseHandler;

	/**
	 * Executes this User HTTPGet request.
	 *
	 * @param environment The {@link Environment} to use to build the HTTP request.
	 * @param user The current user, or null if no user is currently logged in.
	 * @param userId The id of the user you wish to get.
	 * @return The data returned by the {@link ResponseHandler} associated with this request, which
	 * is itself returned by {@link #getResponseHandler()}.
	 */
	public Wrapper<User> execute(Environment environment, User user, String userId) {
		return super.execute(environment, user, userId);
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public String getUrlString(Environment environment, User user, Object... args) throws MalformedURLException {
		return environment.buildUrlString(Endpoint.Type.USER, user, args);
	}

	/**
	 * @inheritDoc
	 */
	@Override
	protected ResponseHandler<Wrapper<User>> getResponseHandler() {
		if (mResponseHandler == null) {
			mResponseHandler = new UserResponseHandler();
		}
		return mResponseHandler;
	}
}
