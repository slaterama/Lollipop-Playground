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

	private UserResponseHandler mResponseHandler = new UserResponseHandler();

	/**
	 * Creates a new GetUserHttpRequest using the specified {@link Environment} and arguments.
	 * @param environment The environment that will be used to execute the request.
	 * @param user The user that is currently logged in to the system, or null if no user is currently logged in.
	 * @param userId The user Id to get.
	 */
	public GetUserHttpRequest(Environment environment, User user, String userId) {
		super(environment, user, userId);
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public String getUrlString(Environment environment, User user, Object... args) throws MalformedURLException {
		return environment.buildUrlString(Endpoint.Type.USER, user, args[0]);
	}

	/**
	 * @inheritDoc
	 */
	@Override
	protected ResponseHandler<Wrapper<User>> getResponseHandler() {
		return mResponseHandler;
	}
}
