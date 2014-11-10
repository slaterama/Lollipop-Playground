package com.citymaps.mobile.android.util;

import android.content.Intent;
import com.citymaps.mobile.android.BuildConfig;
import com.citymaps.mobile.android.model.vo.Config;
import com.citymaps.mobile.android.model.vo.Version;

/**
 * A class for referencing Citymaps-specific Intent actions, categories and extras.
 */
public class IntentUtils {

	/**
	 * A static constant used to build action intent strings.
	 */
	private static final String ACTION = "action";

	/**
	 * A static constant used to build category intent strings.
	 */
	private static final String CATEGORY = "category";

	/**
	 * A static constant used to build extra intent strings.
	 */
	private static final String EXTRA = "extra";

	/**
	 * A static constant describing the application package name.
	 */
	private static final String PACKAGE_NAME = BuildConfig.PACKAGE_NAME; //BuildConfig.class.getPackage().getName();

	/**
	 * A convenience method used to build action intent strings.
	 */
	private static final String makeAction(String name) {
		return String.format("%s.%s.%s", PACKAGE_NAME, ACTION, name);
	}

	/**
	 * A convenience method used to build category intent strings.
	 */
	private static final String makeCategory(String name) {
		return String.format("%s.%s.%s", PACKAGE_NAME, CATEGORY, name);
	}

	/**
	 * A convenience method used to build extra intent strings.
	 */
	private static final String makeExtra(String name) {
		return String.format("%s.%s.%s", PACKAGE_NAME, EXTRA, name);
	}

	/**
	 * Activity Action: Show main activity for the Citymaps application.
	 */
	public static final String ACTION_MAIN = makeAction("MAIN");

	/**
	 * Activity Action: Show settings for the Citymaps application.
	 */
	public static final String ACTION_APP_PREFERENCES = makeAction("APP_PREFERENCES");

	/**
	 * Activity Action: Show developer settings for the Citymaps application.
	 */
	public static final String ACTION_DEVELOPER_PREFERENCES = makeAction("DEVELOPER_PREFERENCES");

	/**
	 * Activity Action: Show Android-specific developer settings for the Citymaps application.
	 */
	public static final String ACTION_ANDROID_PREFERENCES = makeAction("ANDROID_PREFERENCES");

	/**
	 * Service Action: Start or bind to Setup Service.
	 */
	public static final String ACTION_SETUP_SERVICE = makeAction("SETUP_SERVICE");

	/**
	 * Service Action: Start or bind to Setup Service.
	 */
	public static final String ACTION_CITYMAPS_SETUP_SERVICE = makeAction("CITYMAPS_SETUP_SERVICE");

	/**
	 * Service Action: Start or bind to Configuration Service.
	 */
	//public static final String ACTION_CONFIGURATION_SERVICE = makeAction("CONFIGURATION_SERVICE");

	/**
	 * BroadcastReceiver Action: Sent to inform about the status of the setup process.
	 */
	public static final String ACTION_SETUP = makeAction("SETUP");

	/**
	 * BroadcastReceiver Action: Sent to inform about Android config information being loaded.
	 */
	public static final String ACTION_CONFIG_LOADED = makeAction("ACTION_LOADED");

	/**
	 * BroadcastReceiver Action: Attempt to log in to the Citymaps application.
	 */
	public static final String ACTION_LOG_IN = makeAction("LOG_IN");

	/**
	 * The status associated with a setup broadcast.
	 */
	//public static final String EXTRA_SETUP_STATUS = makeExtra("SETUP_STATUS");

	/**
	 * A boolean indicating whether the setup process is complete.
	 */
	//public static final String EXTRA_SETUP_COMPLETE = makeExtra("SETUP_COMPLETE");

	/**
	 * The current Citymaps config information.
	 */
	public static final String EXTRA_CONFIG = makeExtra("CONFIG");

	/**
	 * The current Api status.
	 */
	public static final String EXTRA_API_STATUS = makeExtra("API_STATUS");

	/**
	 * Whether the app is currently completing "first run" processing.
	 */
	public static final String EXTRA_IN_FIRST_RUN = makeExtra("IN_FIRST_RUN");

	public static void putApiStatus(Intent intent, Version status) {
		intent.putExtra(EXTRA_API_STATUS, status);
	}

	public static Version getApiStatus(Intent intent) {
		return intent.getParcelableExtra(EXTRA_API_STATUS);
	}

	public static void putConfig(Intent intent, Config config) {
		intent.putExtra(EXTRA_CONFIG, config);
	}

	public static Config getConfig(Intent intent) {
		return intent.getParcelableExtra(EXTRA_CONFIG);
	}

	public static void putInFirstRun(Intent intent, boolean inFirstRun) {
		intent.putExtra(EXTRA_IN_FIRST_RUN, inFirstRun);
	}

	public static boolean isInFirstRun(Intent intent, boolean defaultValue) {
		return intent.getBooleanExtra(EXTRA_IN_FIRST_RUN, defaultValue);
	}

	private IntentUtils() {
	}
}
