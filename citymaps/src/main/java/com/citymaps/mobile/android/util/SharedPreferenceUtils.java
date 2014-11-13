package com.citymaps.mobile.android.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import com.citymaps.mobile.android.modelnew.Config;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class SharedPreferenceUtils {

	public static SharedPreferences getConfigSharedPreferences(Context context) {
		Context applicationContext = context.getApplicationContext();
		String name = applicationContext.getPackageName() + "_configPreferences";
		return applicationContext.getSharedPreferences(name, Context.MODE_PRIVATE);
	}

	public static boolean getBoolean(SharedPreferences sp, Key key, boolean defValue) {
		return sp.getBoolean(key.toString(), defValue);
	}

	public static float getFloat(SharedPreferences sp, Key key, float defValue) {
		return sp.getFloat(key.toString(), defValue);
	}

	public static int getInt(SharedPreferences sp, Key key, int defValue) {
		return sp.getInt(key.toString(), defValue);
	}

	public static long getLong(SharedPreferences sp, Key key, long defValue) {
		return sp.getLong(key.toString(), defValue);
	}

	public static String getString(SharedPreferences sp, Key key, String defValue) {
		return sp.getString(key.toString(), defValue);
	}

	public static Set<String> getStringSet(SharedPreferences sp, Key key, Set<String> defValues) {
		return sp.getStringSet(key.toString(), defValues);
	}

	@SuppressLint("CommitPrefEdits")
	public static Editor clear(SharedPreferences sp) {
		return sp.edit().clear();
	}

	@SuppressLint("CommitPrefEdits")
	public static Editor putBoolean(SharedPreferences sp, Key key, boolean value) {
		return sp.edit().putBoolean(key.toString(), value);
	}

	@SuppressLint("CommitPrefEdits")
	public static Editor putFloat(SharedPreferences sp, Key key, float value) {
		return sp.edit().putFloat(key.toString(), value);
	}

	@SuppressLint("CommitPrefEdits")
	public static Editor putInt(SharedPreferences sp, Key key, int value) {
		return sp.edit().putInt(key.toString(), value);
	}

	@SuppressLint("CommitPrefEdits")
	public static Editor putLong(SharedPreferences sp, Key key, long value) {
		return sp.edit().putLong(key.toString(), value);
	}

	@SuppressLint("CommitPrefEdits")
	public static Editor putString(SharedPreferences sp, Key key, String value) {
		return sp.edit().putString(key.toString(), value);
	}

	@SuppressLint("CommitPrefEdits")
	public static Editor putStringSet(SharedPreferences sp, Key key, Set<String> values) {
		return sp.edit().putStringSet(key.toString(), values);
	}

	@SuppressLint("CommitPrefEdits")
	public static Editor remove(SharedPreferences sp, Key key) {
		return sp.edit().remove(key.toString());
	}

	/*
	public static Editor putFirstRunComplete(SharedPreferences sp, boolean firstRunComplete) {
		return putBoolean(sp, Key.FIRST_RUN_COMPLETE, firstRunComplete);
	}

	public static boolean isFirstRunComplete(SharedPreferences sp, boolean defValue) {
		return getBoolean(sp, Key.FIRST_RUN_COMPLETE, defValue);
	}
	*/

	public static Editor putCitymapsToken(SharedPreferences sp, String value) {
		return putString(sp, Key.CITYMAPS_TOKEN, value);
	}

	public static String getCitymapsToken(SharedPreferences sp, String defValue) {
		return getString(sp, Key.CITYMAPS_TOKEN, defValue);
	}

	public static Editor putTourProcessed(SharedPreferences sp, boolean value) {
		return putBoolean(sp, Key.TOUR_PROCESSED, value);
	}

	public static boolean isTourProcessed(SharedPreferences sp, boolean defValue) {
		return getBoolean(sp, Key.TOUR_PROCESSED, defValue);
	}

	public static Editor putEnableLocationProcessed(SharedPreferences sp, boolean value) {
		return putBoolean(sp, Key.ENABLE_LOCATION_PROCESSED, value);
	}

	public static boolean isEnableLocationProcessed(SharedPreferences sp, boolean defValue) {
		return getBoolean(sp, Key.ENABLE_LOCATION_PROCESSED, defValue);
	}

	public static Editor putApiVersion(SharedPreferences sp, int value) {
		return putInt(sp, Key.API_VERSION, value);
	}

	public static int getApiVersion(SharedPreferences sp, int defValue) {
		return getInt(sp, Key.API_VERSION, defValue);
	}

	public static Editor putApiBuild(SharedPreferences sp, String value) {
		return putString(sp, Key.API_BUILD, value);
	}

	public static String getApiBuild(SharedPreferences sp, String defValue) {
		return getString(sp, Key.API_BUILD, defValue);
	}

	@SuppressLint("CommitPrefEdits")
	public static Editor putConfig(SharedPreferences sp, Config config) {
		return sp.edit().putInt(Key.CONFIG_APP_VERSION_CODE.toString(), config.getAppVersionCode())
				.putString(Key.CONFIG_APP_VERSION.toString(), config.getAppVersion())
				.putInt(Key.CONFIG_MIN_VERSION_CODE.toString(), config.getMinVersionCode())
				.putString(Key.CONFIG_MIN_VERSION.toString(), config.getMinVersion())
				.putLong(Key.CONFIG_TIMESTAMP.toString(), config.getTimestamp())
				.putString(Key.CONFIG_UPGRADE_PROMPT.toString(), config.getUpgradePrompt());
	}

	public static Config getConfig(SharedPreferences sp) {
		Map<String, Object> map = new HashMap<String, Object>(6);
		map.put("app_version_code", sp.getInt(Key.CONFIG_APP_VERSION_CODE.toString(), 0));
		map.put("min_version_code", sp.getInt(Key.CONFIG_MIN_VERSION_CODE.toString(), 0));
		map.put("app_version", sp.getString(Key.CONFIG_APP_VERSION.toString(), ""));
		map.put("min_version", sp.getString(Key.CONFIG_MIN_VERSION.toString(), ""));
		map.put("timestamp", sp.getLong(Key.CONFIG_TIMESTAMP.toString(), 0));
		map.put("upgradePrompt", sp.getString(Key.CONFIG_UPGRADE_PROMPT.toString(), ""));
		String json = new JSONObject(map).toString();
		return GsonUtils.getGson().fromJson(json, Config.class);
	}

	public static long getConfigTimestamp(SharedPreferences sp, long defValue) {
		return getLong(sp, Key.CONFIG_TIMESTAMP, defValue);
	}

	public static Editor putProcessedAction(SharedPreferences sp, int action) {
		return putInt(sp, Key.CONFIG_PROCESSED_ACTION, action);
	}

	public static int getProcessedAction(SharedPreferences sp, int defValue) {
		return getInt(sp, Key.CONFIG_PROCESSED_ACTION, defValue);
	}

	public static Editor putProcessedTimestamp(SharedPreferences sp, long timestamp) {
		return putLong(sp, Key.CONFIG_PROCESSED_TIMESTAMP, timestamp);
	}

	public static long getProcessedTimestamp(SharedPreferences sp, int defValue) {
		return getLong(sp, Key.CONFIG_PROCESSED_TIMESTAMP, defValue);
	}

	private SharedPreferenceUtils() {
	}

	public static enum Key {
		/*
		FIRST_RUN_COMPLETE,
		*/

		CITYMAPS_TOKEN,

		TOUR_PROCESSED,
		ENABLE_LOCATION_PROCESSED,

		API_VERSION,
		API_BUILD,
		CONFIG_APP_VERSION,
		CONFIG_APP_VERSION_CODE,
		CONFIG_MIN_VERSION,
		CONFIG_MIN_VERSION_CODE,
		CONFIG_TIMESTAMP,
		CONFIG_UPGRADE_PROMPT,
		CONFIG_PROCESSED_ACTION,
		CONFIG_PROCESSED_TIMESTAMP;

		private String mToString;

		private static Key fromString(String string) {
			String name = string.replaceAll("^pref_", "").toUpperCase();
			return Key.valueOf(name);
		}

		private Key() {
			mToString = String.format("pref_%s", name().toLowerCase());
		}

		@Override
		public String toString() {
			return mToString;
		}
	}
}
