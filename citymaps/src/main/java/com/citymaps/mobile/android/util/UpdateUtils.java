package com.citymaps.mobile.android.util;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import com.citymaps.mobile.android.BuildConfig;
import com.citymaps.mobile.android.model.vo.Config;

public class UpdateUtils {

	public static UpdateType getUpdateType(Config config) {
		if (BuildConfig.VERSION_CODE < config.getMinVersionCode()) {
			return UpdateType.HARD;
		} else if (BuildConfig.VERSION_CODE < config.getAppVersionCode() || true) { // TODO TEMP
			return UpdateType.SOFT;
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
		SOFT
	}
}
