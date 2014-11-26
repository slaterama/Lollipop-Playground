package com.citymaps.mobile.android.util;

import java.util.HashMap;
import java.util.Map;

public enum CitymapsPreference {

	/* General Preferences */

	SHARE_APP("pref_share_app"),
	ABOUT("pref_about"),
	ADD_BUSINESS("pref_add_business"),
	POLICY("pref_policy"),
	TERMS("pref_terms"),
	CREDITS("pref_credits"),
	DEVELOPER_MODE("pref_developer_mode"),

	/* Signed-out Preferences */

	SIGNIN("pref_signin"),

	/* Signed-in Preferences */

	EDIT_PROFILE("pref_edit_profile"),
	CHANGE_PASSWORD("pref_change_password"),
	CONNECT_THIRD_PARTY_APPS("pref_connect_third_party_apps"),
	INVITE_FRIENDS("invite_friends"),
	SIGNOUT("pref_signout"),

	/* Non-visible Preferences */

	CITYMAPS_TOKEN("pref_citymaps_token"),
	FACEBOOK_TOKEN("pref_facebook_token"),
	GOOGLE_TOKEN("pref_google_token"),
	TOUR_PROCESSED("pref_tour_processed"),
	ENABLE_LOCATION_PROCESSED("pref_enable_location_processed"),
	API_VERSION("pref_api_version"),
	API_BUILD("pref_api_build"),
	CONFIG_APP_VERSION("pref_config_app_version"),
	CONFIG_APP_VERSION_CODE("pref_config_app_version_code"),
	CONFIG_MIN_VERSION("pref_config_min_version"),
	CONFIG_MIN_VERSION_CODE("pref_config_min_version_code"),
	CONFIG_TIMESTAMP("pref_config_timestamp"),
	CONFIG_UPGRADE_PROMPT("pref_config_upgrade_prompt"),
	CONFIG_PROCESSED_ACTION("pref_config_processed_action"),
	CONFIG_PROCESSED_TIMESTAMP("pref_config_processed_timestamp");

	private static Map<String, CitymapsPreference> mKeyMap;

	private static CitymapsPreference fromKey(String key) {
		if (mKeyMap == null) {
			CitymapsPreference[] values = values();
			mKeyMap = new HashMap<String, CitymapsPreference>(values.length);
			for (CitymapsPreference value : values) {
				mKeyMap.put(value.mKey, value);
			}
		}
		return mKeyMap.get(key);
	}

	private String mKey;

	private CitymapsPreference(String key) {
		mKey = key;
	}

	public String getKey() {
		return mKey;
	}
}
