package com.citymaps.mobile.android.notused_provider.config;

import android.content.*;
import android.database.Cursor;
import android.net.Uri;
import com.citymaps.mobile.android.notused_provider.config.ConfigContract.Settings;

public class ConfigProvider extends ContentProvider {

	private static final UriMatcher sUriMatcher = buildUriMatcher();

	private static final int SETTINGS = 100;
	private static final int SETTINGS_KEY = 101;

	private static UriMatcher buildUriMatcher() {
		final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
		final String authority = ConfigContract.CONTENT_AUTHORITY;

		matcher.addURI(authority, "settings", SETTINGS);
		matcher.addURI(authority, "settings/*", SETTINGS_KEY);

		return matcher;
	}

	private ConfigProviderHelper mHelper;

	@Override
	public boolean onCreate() {
		mHelper = ConfigProviderHelper.newInstance(getContext());
		return true;
	}

	@Override
	public String getType(Uri uri) {
		int match = sUriMatcher.match(uri);
		switch (match) {
			case SETTINGS:
				return Settings.CONTENT_TYPE;
			case SETTINGS_KEY:
				return Settings.CONTENT_ITEM_TYPE;
			default:
				throw new UnsupportedOperationException("Unknown uri: " + uri);
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		final int match = sUriMatcher.match(uri);
		boolean syncToNetwork = !ConfigContract.hasCallerIsSyncAdapterParameter(uri);
		switch (match) {
			case SETTINGS:
				String key = values.getAsString(Settings.KEY);
				Object value = values.get(Settings.VALUE);
				mHelper.insertSetting(key, value);
				getContext().getContentResolver().notifyChange(uri, null, syncToNetwork);
				return Settings.buildSettingUri(key);
			default: {
				throw new UnsupportedOperationException("Unknown uri: " + uri);
			}
		}
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
}
