package com.citymaps.mobile.android.util;

import android.app.Activity;
import android.content.Intent;
import com.citymaps.mobile.android.BuildConfig;
import com.citymaps.mobile.android.model.vo.Config;
import com.citymaps.mobile.android.util.softupdatecompat.SoftUpdateCompat;
import com.citymaps.mobile.android.view.HardUpdateActivity;

public class UpdateUtils {

	public static UpdateType getUpdateType(Config config) {
		if (BuildConfig.VERSION_CODE < config.getMinVersionCode()) {
			return UpdateType.HARD;
		} else if (BuildConfig.VERSION_CODE < config.getAppVersionCode() + 10) { // TODO TEMP TEMP TEMP TEMP + 10
			return UpdateType.SOFT;
		} else {
			return UpdateType.NONE;
		}
	}

	public static void processConfig(Activity activity, Config config, boolean showDialogIfSoft) {
		UpdateType type = getUpdateType(config);
		switch (type) {
			case HARD:
				if (activity != null) {
					activity.startActivity(new Intent(activity, HardUpdateActivity.class));
					activity.finish();
				}
				break;
			case SOFT:
				if (activity != null && showDialogIfSoft) {
					SoftUpdateCompat.newInstance(activity).showSoftUpdateDialogFragment(config);
				}
				break;
			case NONE:
			default:
				// No action needed
		}
	}

	public static enum UpdateType {
		NONE,
		SOFT,
		HARD
	}
}
