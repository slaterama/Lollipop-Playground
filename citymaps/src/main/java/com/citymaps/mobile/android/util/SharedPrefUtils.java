package com.citymaps.mobile.android.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import com.citymaps.mobile.android.model.Config;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class SharedPrefUtils {

	public static SharedPreferences getConfigSharedPreferences(Context context) {
		Context applicationContext = context.getApplicationContext();
		String name = applicationContext.getPackageName() + "_configPreferences";
		return applicationContext.getSharedPreferences(name, Context.MODE_PRIVATE);
	}

	/* Generic get methods */

	public static boolean getBoolean(SharedPreferences sp, CitymapsPreference preference, boolean defValue) {
		return sp.getBoolean(preference.getKey(), defValue);
	}

	public static float getFloat(SharedPreferences sp, CitymapsPreference preference, float defValue) {
		return sp.getFloat(preference.getKey(), defValue);
	}

	public static int getInt(SharedPreferences sp, CitymapsPreference preference, int defValue) {
		return sp.getInt(preference.getKey(), defValue);
	}

	public static long getLong(SharedPreferences sp, CitymapsPreference preference, long defValue) {
		return sp.getLong(preference.getKey(), defValue);
	}

	public static String getString(SharedPreferences sp, CitymapsPreference preference, String defValue) {
		return sp.getString(preference.getKey(), defValue);
	}

	public static Set<String> getStringSet(SharedPreferences sp, CitymapsPreference preference, Set<String> defValues) {
		return sp.getStringSet(preference.getKey(), defValues);
	}

	/* Generic put methods */

	@SuppressLint("CommitPrefEdits")
	public static Editor putBoolean(Editor editor, CitymapsPreference preference, boolean value) {
		return editor.putBoolean(preference.getKey(), value);
	}

	@SuppressLint("CommitPrefEdits")
	public static Editor putFloat(Editor editor, CitymapsPreference preference, float value) {
		return editor.putFloat(preference.getKey(), value);
	}

	@SuppressLint("CommitPrefEdits")
	public static Editor putInt(Editor editor, CitymapsPreference preference, int value) {
		return editor.putInt(preference.getKey(), value);
	}

	@SuppressLint("CommitPrefEdits")
	public static Editor putLong(Editor editor, CitymapsPreference preference, long value) {
		return editor.putLong(preference.getKey(), value);
	}

	@SuppressLint("CommitPrefEdits")
	public static Editor putString(Editor editor, CitymapsPreference preference, String value) {
		return editor.putString(preference.getKey(), value);
	}

	@SuppressLint("CommitPrefEdits")
	public static Editor putStringSet(Editor editor, CitymapsPreference preference, Set<String> values) {
		return editor.putStringSet(preference.getKey(), values);
	}

	@SuppressLint("CommitPrefEdits")
	public static Editor remove(Editor editor, CitymapsPreference preference) {
		return editor.remove(preference.getKey());
	}

	@SuppressLint("CommitPrefEdits")
	public static Editor putConfig(Editor editor, Config config) {
		return editor.putInt(CitymapsPreference.CONFIG_APP_VERSION_CODE.getKey(), config.getAppVersionCode())
				.putString(CitymapsPreference.CONFIG_APP_VERSION.getKey(), config.getAppVersion())
				.putInt(CitymapsPreference.CONFIG_MIN_VERSION_CODE.getKey(), config.getMinVersionCode())
				.putString(CitymapsPreference.CONFIG_MIN_VERSION.getKey(), config.getMinVersion())
				.putLong(CitymapsPreference.CONFIG_TIMESTAMP.getKey(), config.getTimestamp())
				.putString(CitymapsPreference.CONFIG_UPGRADE_PROMPT.getKey(), config.getUpgradePrompt());
	}

	public static Config getConfig(SharedPreferences sp) {
		Map<String, Object> map = new HashMap<String, Object>(6);
		map.put("app_version_code", SharedPrefUtils.getInt(sp, CitymapsPreference.CONFIG_APP_VERSION_CODE, 0));
		map.put("min_version_code", SharedPrefUtils.getInt(sp, CitymapsPreference.CONFIG_MIN_VERSION_CODE, 0));
		map.put("app_version", SharedPrefUtils.getString(sp, CitymapsPreference.CONFIG_APP_VERSION, ""));
		map.put("min_version", SharedPrefUtils.getString(sp, CitymapsPreference.CONFIG_MIN_VERSION, ""));
		map.put("timestamp", SharedPrefUtils.getLong(sp, CitymapsPreference.CONFIG_TIMESTAMP, 0));
		map.put("upgradePrompt", SharedPrefUtils.getString(sp, CitymapsPreference.CONFIG_UPGRADE_PROMPT, ""));
		String json = new JSONObject(map).toString();
		return GsonUtils.getGson().fromJson(json, Config.class);
	}

	private SharedPrefUtils() {
	}
}
