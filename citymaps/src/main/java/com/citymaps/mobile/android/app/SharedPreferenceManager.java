//  This singleton implements the "Parametric initialization-on-demand holder idiom" for singletons
//  with immutable parameters in Java (in this case, the application context) as described here:
//  http://unafbapune.blogspot.com/2007/09/parametric-initialization-on-demand.html

package com.citymaps.mobile.android.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.HashMap;
import java.util.Map;

public class SharedPreferenceManager {

	private static Context sContext;

	public static synchronized SharedPreferenceManager getInstance(Context context) {
		sContext = context.getApplicationContext();
		return LazyHolder.INSTANCE;
	}

	private static final class LazyHolder {
		private static final SharedPreferenceManager INSTANCE = new SharedPreferenceManager();
	}

	private SharedPreferences mDefaultSharedPreferences;

	private SharedPreferenceManager() {
		mDefaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(sContext);
	}

	public Context getContext() {
		return sContext;
	}

	public SharedPreferences getDefaultSharedPreferences() {
		return mDefaultSharedPreferences;
	}

	public void applyApiVersion(int apiVersion) {
		mDefaultSharedPreferences.edit()
				.putInt(PreferenceType.API_VERSION.getKey(), apiVersion)
				.apply();
	}

	public boolean commitApiVersion(int apiVersion) {
		return mDefaultSharedPreferences.edit()
				.putInt(PreferenceType.API_VERSION.getKey(), apiVersion)
				.commit();
	}

	public int getApiVersion(int defValue) {
		return mDefaultSharedPreferences.getInt(PreferenceType.API_VERSION.getKey(), defValue);
	}

	public void applyApiBuild(String apiBuild) {
		mDefaultSharedPreferences.edit()
				.putString(PreferenceType.API_BUILD.getKey(), apiBuild)
				.apply();
	}

	public boolean commitApiBuild(String apiBuild) {
		return mDefaultSharedPreferences.edit()
				.putString(PreferenceType.API_BUILD.getKey(), apiBuild)
				.commit();
	}

	public String getApiBuild(String defValue) {
		return mDefaultSharedPreferences.getString(PreferenceType.API_BUILD.getKey(), defValue);
	}

	public static enum PreferenceType {
		API_VERSION("pref_api_version"),
		API_BUILD("pref_api_build");

		private static Map<String, PreferenceType> mKeyMap;

		private static PreferenceType fromKey(String key) {
			if (mKeyMap == null) {
				PreferenceType[] values = PreferenceType.values();
				mKeyMap = new HashMap<String, PreferenceType>(values.length);
				for (PreferenceType preference : values) {
					mKeyMap.put(preference.mKey, preference);
				}
			}
			return mKeyMap.get(key);
		}

		private String mKey;

		private PreferenceType(String key) {
			mKey = key;
		}

		public String getKey() {
			return mKey;
		}
	}
}
