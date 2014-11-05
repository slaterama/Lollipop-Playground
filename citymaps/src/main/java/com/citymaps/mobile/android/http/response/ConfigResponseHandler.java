package com.citymaps.mobile.android.http.response;

import com.citymaps.mobile.android.app.DataWrapper;
import com.citymaps.mobile.android.app.Wrapper;
import com.citymaps.mobile.android.model.vo.Config;
import com.google.gson.JsonElement;

public class ConfigResponseHandler extends CitymapsResponseHandler<Config> {

	@Override
	protected Wrapper<Config, Exception> wrapResult(JsonElement json) {
		Config config = getGson().fromJson(json, Config.class);
		return new DataWrapper<Config, Exception>(config);
	}
}
