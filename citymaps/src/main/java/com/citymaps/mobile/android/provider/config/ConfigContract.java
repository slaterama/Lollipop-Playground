package com.citymaps.mobile.android.provider.config;

import android.net.Uri;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.text.TextUtils;

public class ConfigContract {

	interface SettingsColumns {
		String KEY = "setting_key";
		String VALUE = "setting_value";
	}

	public static final String CONTENT_AUTHORITY = "com.citymaps.mobile.android.provider.config";

	public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

	private static final String PATH_SETTINGS = "settings";

	public static class Settings implements SettingsColumns, BaseColumns {
		public static final Uri CONTENT_URI =
				BASE_CONTENT_URI.buildUpon().appendPath(PATH_SETTINGS).build();

		public static final String CONTENT_TYPE =
				"vnd.android.cursor.dir/vnd.citymaps.config.setting";
		public static final String CONTENT_ITEM_TYPE =
				"vnd.android.cursor.item/vnd.citymaps.config.setting";

		public static Uri buildSettingUri(String settingKey) {
			return CONTENT_URI.buildUpon().appendPath(settingKey).build();
		}
	}

	public static Uri addCallerIsSyncAdapterParameter(Uri uri) {
		return uri.buildUpon().appendQueryParameter(
				ContactsContract.CALLER_IS_SYNCADAPTER, "true").build();
	}

	public static boolean hasCallerIsSyncAdapterParameter(Uri uri) {
		return TextUtils.equals("true",
				uri.getQueryParameter(ContactsContract.CALLER_IS_SYNCADAPTER));
	}

	private ConfigContract() {
	}
}
