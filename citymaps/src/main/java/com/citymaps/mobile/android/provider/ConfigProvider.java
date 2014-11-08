package com.citymaps.mobile.android.provider;

import android.content.*;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;
import com.citymaps.mobile.android.util.LogEx;

public class ConfigProvider extends ContentProvider {

	private static final String AUTHORITY = "com.citymaps.mobile.android.provider.config";

	private static final String URL = String.format("content://%s/config", AUTHORITY);

	public static final Uri CONTENT_URI = Uri.parse(URL);

	private static final String _ID = "_id";
	public static final String KEY = "key";
	public static final String VALUE = "value";

	static final int SETTINGS = 1;
	static final int SETTING_KEY = 2;

	static final UriMatcher URI_MATCHER;
	static {
		URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
		URI_MATCHER.addURI(AUTHORITY, "settings", SETTINGS);
		URI_MATCHER.addURI(AUTHORITY, "settings/*", SETTING_KEY);
	}

	private SharedPreferences mSharedPreferences;

    public ConfigProvider() {
	}

	@Override
	public boolean onCreate() {
		mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
		return (mSharedPreferences != null);
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		SharedPreferences.Editor editor = mSharedPreferences.edit();

		String key = values.getAsString(KEY);
		Object value = values.get(VALUE);
		if (value instanceof Boolean) {
			mSharedPreferences.edit().putBoolean(key, (Boolean) value).apply();
		} else if (value instanceof Float) {
			mSharedPreferences.edit().putFloat(key, (Float) value).apply();
		} else if (value instanceof Long) {
			mSharedPreferences.edit().putFloat(key, (Long) value).apply();
		} else if (value instanceof Integer) {
			mSharedPreferences.edit().putInt(key, (Integer) value).apply();
		} else if (value instanceof String) {
			mSharedPreferences.edit().putString(key, (String) value).apply();
		} else {
			throw new IllegalArgumentException("value passed to insert is not a valid SharedPreferences value");
		}

		Uri result = ContentUris.withAppendedId(uri, 0);
		return result;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
						String[] selectionArgs, String sortOrder) {
		// TODO: Implement this to handle query requests from clients.
		throw new UnsupportedOperationException("Not yet implemented");
	}

	@Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Implement this to handle requests to delete one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
            String[] selectionArgs) {
        // TODO: Implement this to handle requests to update one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }

	@Override
	public String getType(Uri uri) {
		int match = URI_MATCHER.match(uri);
		switch (match) {
			case SETTINGS:
				return "vnd.android.cursor.dir/settings";
			case SETTING_KEY:
				return "vnd.android.cursor.item/settings";
			default:
				throw new IllegalArgumentException("Unsupported URI: " + uri);
		}
	}
}
