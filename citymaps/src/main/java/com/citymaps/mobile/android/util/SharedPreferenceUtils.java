package com.citymaps.mobile.android.util;

import android.content.Context;
import android.content.SharedPreferences;
import com.citymaps.mobile.android.model.vo.Config;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SharedPreferenceUtils {

	public static SharedPreferences getConfigSharedPreferences(Context context) {
		Context applicationContext = context.getApplicationContext();
		String name = String.format("%s_configPreferences", applicationContext.getPackageName());
		return applicationContext.getSharedPreferences(name, Context.MODE_PRIVATE);
	}

	public static void applyApiVersion(SharedPreferences sharedPreferences, int apiVersion) {
		sharedPreferences.edit().putInt(PreferenceType.API_VERSION.getKey(), apiVersion).apply();
	}

	public static boolean commitApiVersion(SharedPreferences sharedPreferences, int apiVersion) {
		return sharedPreferences.edit().putInt(PreferenceType.API_VERSION.getKey(), apiVersion).commit();
	}

	public static int getApiVersion(SharedPreferences sharedPreferences, int defValue) {
		return sharedPreferences.getInt(PreferenceType.API_VERSION.getKey(), defValue);
	}

	public static void applyApiBuild(SharedPreferences sharedPreferences, String apiBuild) {
		sharedPreferences.edit().putString(PreferenceType.API_BUILD.getKey(), apiBuild).apply();
	}

	public static boolean commitApiBuild(SharedPreferences sharedPreferences, String apiBuild) {
		return sharedPreferences.edit().putString(PreferenceType.API_BUILD.getKey(), apiBuild).commit();
	}

	public static String getApiBuild(SharedPreferences sharedPreferences, String defValue) {
		return sharedPreferences.getString(PreferenceType.API_BUILD.getKey(), defValue);
	}

	private static SharedPreferences.Editor configEditor(Context context, Config config) {
		return getConfigSharedPreferences(context).edit()
				.putInt(PreferenceType.CONFIG_APP_VERSION_CODE.getKey(), config.getAppVersionCode())
				.putString(PreferenceType.CONFIG_APP_VERSION.getKey(), config.getAppVersion())
				.putInt(PreferenceType.CONFIG_MIN_VERSION_CODE.getKey(), config.getMinVersionCode())
				.putString(PreferenceType.CONFIG_MIN_VERSION.getKey(), config.getMinVersion())
				.putLong(PreferenceType.CONFIG_TIMESTAMP.getKey(), config.getTimestamp())
				.putString(PreferenceType.CONFIG_UPGRADE_PROMPT.getKey(), config.getUpgradePrompt());
	}

	public static void applyConfig(Context context, Config config) {
		configEditor(context, config).apply();
	}

	public static boolean commitConfig(Context context, Config config) {
		return configEditor(context, config).commit();
	}

	public static Config getConfig(Context context) {
		SharedPreferences sharedPreferences = getConfigSharedPreferences(context);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("app_version_code", sharedPreferences.getInt(PreferenceType.CONFIG_APP_VERSION_CODE.getKey(), 0));
		map.put("min_version_code", sharedPreferences.getInt(PreferenceType.CONFIG_MIN_VERSION_CODE.getKey(), 0));
		map.put("app_version", sharedPreferences.getString(PreferenceType.CONFIG_APP_VERSION.getKey(), ""));
		map.put("min_version", sharedPreferences.getString(PreferenceType.CONFIG_MIN_VERSION.getKey(), ""));
		map.put("timestamp", sharedPreferences.getLong(PreferenceType.CONFIG_TIMESTAMP.getKey(), 0));
		map.put("upgradePrompt", sharedPreferences.getString(PreferenceType.CONFIG_UPGRADE_PROMPT.getKey(), ""));
		String json = new JSONObject(map).toString();
		return GsonUtils.getGson().fromJson(json, Config.class);
	}

	public static void applyLastDismissedVersionCode(Context context, int code) {
		getConfigSharedPreferences(context).edit()
				.putInt(PreferenceType.CONFIG_LAST_DISMISSED_VERSION_CODE.getKey(), code)
				.apply();
	}

	public static boolean commitLastDismissedVersionCode(Context context, int code) {
		return getConfigSharedPreferences(context).edit()
				.putInt(PreferenceType.CONFIG_LAST_DISMISSED_VERSION_CODE.getKey(), code)
				.commit();
	}

	public static int getLastDismissedVersionCode(Context context, int defValue) {
		return getConfigSharedPreferences(context)
				.getInt(PreferenceType.CONFIG_LAST_DISMISSED_VERSION_CODE.getKey(), defValue);
	}

	private SharedPreferenceUtils() {
	}

	public static enum PreferenceType {
		API_VERSION("pref_api_version"),
		API_BUILD("pref_api_build"),
		CONFIG_APP_VERSION("pref_config_app_version"),
		CONFIG_APP_VERSION_CODE("pref_config_app_version_code"),
		CONFIG_MIN_VERSION("pref_config_min_version"),
		CONFIG_MIN_VERSION_CODE("pref_config_min_version_code"),
		CONFIG_TIMESTAMP("pref_config_timestamp"),
		CONFIG_UPGRADE_PROMPT("pref_config_upgrade_prompt"),
		CONFIG_LAST_DISMISSED_VERSION_CODE("pref_config_last_dismissed_version_code");

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