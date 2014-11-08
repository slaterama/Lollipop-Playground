package com.citymaps.mobile.android.provider.config;

import android.content.Context;
import android.content.SharedPreferences;

public class ConfigProviderHelperSharedPreferences extends ConfigProviderHelper {

	private SharedPreferences mSharedPreferences;

	public ConfigProviderHelperSharedPreferences(Context context) {
		super(context);
		String name = context.getPackageName() + "_configPreferences";
		mSharedPreferences = context.getSharedPreferences(name, Context.MODE_PRIVATE);
	}

	@Override
	public void insertSetting(String key, Object value) {
		if (value instanceof String) {
			mSharedPreferences.edit().putString(key, (String) value).apply();
		} else if (value instanceof Integer) {
			mSharedPreferences.edit().putInt(key, (Integer) value).apply();
		} else if (value instanceof Boolean) {
			mSharedPreferences.edit().putBoolean(key, (Boolean) value).apply();
		} else if (value instanceof Float) {
			mSharedPreferences.edit().putFloat(key, (Float) value).apply();
		} else if (value instanceof Long) {
			mSharedPreferences.edit().putFloat(key, (Long) value).apply();
		} else {
			throw new IllegalArgumentException("value passed to insertSetting is not a valid SharedPreferences value");
		}
	}
}
