package com.citymaps.mobile.android.util;

import android.content.*;
import android.net.Uri;
import android.util.SparseArray;
import com.citymaps.mobile.android.BuildConfig;
import com.citymaps.mobile.android.model.Config;

public class UpdateUtils {

	private static final long MILLIS_PER_MINUTE = 60000;
	private static final long MILLIS_PER_HOUR = MILLIS_PER_MINUTE * 60;
	private static final long MILLIS_PER_DAY = MILLIS_PER_HOUR * 24;
	private static final long LATER_DURATION = MILLIS_PER_DAY * 2; // Two days

	public static UpdateType getUpdateType(Context context, Config config) {
		if (BuildConfig.VERSION_CODE < config.getMinVersionCode()) {
			return UpdateType.HARD;
		} else if (BuildConfig.VERSION_CODE < config.getAppVersionCode()) {
			SharedPreferences sp = SharedPrefUtils.getConfigSharedPreferences(context);
			UpdateAction action = UpdateAction.fromAction(
					SharedPrefUtils.getInt(sp, CitymapsPreference.CONFIG_PROCESSED_ACTION,
							UpdateAction.NONE.getAction()));
			switch (action) {
				case SKIP:
					// User skipped this update
					return UpdateType.SKIPPED;
				case UPDATE:
					// User selected UPDATE, but no update was made. Treat it the same as LATER
				case LATER:
					long processedTimestamp = SharedPrefUtils.getLong(sp,
							CitymapsPreference.CONFIG_PROCESSED_TIMESTAMP, 0);
					long diff = System.currentTimeMillis() - processedTimestamp;
					if (diff > LATER_DURATION) {
						return UpdateType.SOFT;
					} else {
						return UpdateType.SKIPPED;
					}
				case NONE:
				default:
					return UpdateType.SOFT;
			}
		} else {
			return UpdateType.NONE;
		}
	}

	public static void goToPlayStore(Context context) {
		try {
			String urlString = "market://details?id=" + BuildConfig.PLAY_STORE_ID;
			context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(urlString)));
		} catch (ActivityNotFoundException e) {
			String urlString = "http://play.google.com/store/apps/details?id=" + BuildConfig.PLAY_STORE_ID;
			context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(urlString)));
		}
	}

	private UpdateUtils() {
	}

	public static enum UpdateType {
		NONE,
		HARD,
		SOFT,
		SKIPPED
	}

	public static enum UpdateAction {
		NONE(0),
		SKIP(DialogInterface.BUTTON_NEGATIVE),
		LATER(DialogInterface.BUTTON_NEUTRAL),
		UPDATE(DialogInterface.BUTTON_POSITIVE);

		private static SparseArray<UpdateAction> mActionArray;

		public static UpdateAction fromAction(int action) {
			if (mActionArray == null) {
				UpdateAction[] values = UpdateAction.values();
				mActionArray = new SparseArray<UpdateAction>(values.length);
				for (UpdateAction value : values) {
					mActionArray.put(value.getAction(), value);
				}
			}
			return mActionArray.get(action);
		}

		private int mAction;

		private UpdateAction(int action) {
			mAction = action;
		}

		public int getAction() {
			return mAction;
		}
	}
}
