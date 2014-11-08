package com.citymaps.mobile.android.provider.config;

import android.content.Context;

public abstract class ConfigProviderHelper {

	public static ConfigProviderHelper newInstance(Context context) {
		return new ConfigProviderHelperSharedPreferences(context);
	}

	protected Context mContext;

	public ConfigProviderHelper(Context context) {
		super();
		mContext = context;
	}

	public abstract void insertSetting(String key, Object value);

}
