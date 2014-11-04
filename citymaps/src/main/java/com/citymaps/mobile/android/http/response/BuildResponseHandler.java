package com.citymaps.mobile.android.http.response;

import com.citymaps.mobile.android.app.DataWrapper;
import com.citymaps.mobile.android.app.Wrapper;
import com.citymaps.mobile.android.model.vo.ApiBuild;
import com.citymaps.mobile.android.model.vo.Version;
import com.google.gson.JsonElement;

/**
 * A ResponseHandler class designed to handle a ApiBuild HTTP response.
 */
public class BuildResponseHandler extends CitymapsResponseHandler<ApiBuild> {

	/**
	 * Wraps Development build data.
	 *
	 * @param json The {@link com.google.gson.JsonElement} that contains the content resulting from the HTTP request.
	 * @return The wrapped build data.
	 */
	@Override
	protected Wrapper<ApiBuild, Exception> wrapResult(JsonElement json) {
		Version version = getGson().fromJson(json, Version.class);
		ApiBuild build = new ApiBuild(version.getVersion(), version.getBuild());
		return new DataWrapper<ApiBuild, Exception>(build);
	}
}
