package com.citymaps.mobile.android.view.settings;

import com.citymaps.mobile.android.preference.PreferenceFragment;

import java.util.HashMap;
import java.util.Map;

public class PreferencesFragment extends PreferenceFragment {

	public static enum PreferenceType {
		SHARE_APP("pref_share_app"),
		CONNECT_FACEBOOK("pref_connect_facebook"),
		CONNECT_GOOGLE("pref_connect_google"),
		CONNECT_TWITTER("pref_connect_twitter"),
		EMAIL_NOTIFICATIONS("pref_email_notifications"),
		ABOUT("pref_about"),
		SUPPORT("pref_support"),
		TOUR("pref_tour"),
		FEEDBACK("pref_feedback"),
		POLICY("pref_policy"),
		TERMS("pref_terms"),
		CREDITS("pref_credits"),
		DEVELOPER_MODE("pref_developer_mode"),
		SIGNIN("pref_signin"),
		SIGNOUT("pref_signout");

		private static Map<String, PreferenceType> sPreferenceTypeMap;

		public static PreferenceType fromKey(String key) {
			if (sPreferenceTypeMap == null) {
				PreferenceType[] types = values();
				sPreferenceTypeMap = new HashMap<String, PreferenceType>(types.length);
				for (PreferenceType type : types) {
					sPreferenceTypeMap.put(type.mKey, type);
				}
			}
			return sPreferenceTypeMap.get(key);
		}

		private String mKey;

		private PreferenceType(String key) {
			mKey = key;
		}

		@Override
		public String toString() {
			return mKey;
		}
	}
}
