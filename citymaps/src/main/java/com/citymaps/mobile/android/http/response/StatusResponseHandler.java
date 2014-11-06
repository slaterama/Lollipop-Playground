package com.citymaps.mobile.android.http.response;

import com.citymaps.mobile.android.app.DataWrapper;
import com.citymaps.mobile.android.app.Wrapper;
import com.citymaps.mobile.android.model.vo.ApiStatus;
import com.citymaps.mobile.android.model.vo.Version;
import com.google.gson.JsonElement;

/**
 * A ResponseHandler class designed to handle a ApiBuild HTTP response.
 */
public class StatusResponseHandler extends CitymapsResponseHandler<ApiStatus> {

	/**
	 * Wraps Development build data.
	 *
	 * @param json The {@link com.google.gson.JsonElement} that contains the content resulting from the HTTP request.
	 * @return The wrapped build data.
	 */
	@Override
	protected Wrapper<ApiStatus, Exception> wrapResult(JsonElement json) {
		Version version = getGson().fromJson(json, Version.class);
		ApiStatus build = new ApiStatus(version.getVersion(), version.getBuild());
		return new DataWrapper<ApiStatus, Exception>(build);
	}
}
