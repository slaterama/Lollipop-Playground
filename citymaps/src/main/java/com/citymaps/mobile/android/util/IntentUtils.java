package com.citymaps.mobile.android.util;

import android.content.Intent;
import com.citymaps.mobile.android.BuildConfig;
import com.citymaps.mobile.android.model.Config;
import com.citymaps.mobile.android.model.ThirdPartyUser;

/**
 * A class for referencing Citymaps-specific Intent actions, categories and extras.
 */
public class IntentUtils {

	private static final String PROPERTY_NAME_EMAIL = "email";

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
	 * BroadcastReceiver Action: Sent to inform about Android config information being loaded.
	 */
	public static final String ACTION_CONFIG_LOADED = makeAction("ACTION_LOADED");

	/**
	 * The current Citymaps config information.
	 */
	public static final String EXTRA_CONFIG = makeExtra("CONFIG");

	/**
	 * An integer that specifies in what mode the Login activity should open.
	 * <p>Values are:
	 * <ul>
	 *     <li>{@code LoginActivity.SIGN_IN_MODE}</li>
	 *     <li>{@code LoginActivity.CREATE_ACCOUNT_MODE}</li>
	 *     <li>{@code LoginActivity.RESET_PASSWORD_MODE}</li>
	 * </ul>
	 */
	public static final String EXTRA_LOGIN_MODE = makeExtra("LOGIN_MODE");

	/**
	 * Whether the app is currently in "startup mode" (i.e. walking the user through
	 * a series of initial screens including LaunchActivity, TourActivity, etc.)
	 */
	public static final String EXTRA_STARTUP_MODE = makeExtra("STARTUP_MODE");

	public static final String EXTRA_THIRD_PARTY_USER = makeExtra("THIRD_PARTY_USER");

	public static final String EXTRA_ERROR_MESSAGE = makeExtra("ERROR_MESSAGE");

	public static void putConfig(Intent intent, Config config) {
		intent.putExtra(EXTRA_CONFIG, config);
	}

	public static Config getConfig(Intent intent) {
		return intent.getParcelableExtra(EXTRA_CONFIG);
	}

	public static void putLoginMode(Intent intent, int loginMode) {
		intent.putExtra(EXTRA_LOGIN_MODE, loginMode);
	}

	public static int getLoginMode(Intent intent, int defaultValue) {
		return intent.getIntExtra(EXTRA_LOGIN_MODE, defaultValue);
	}

	public static void putStartupMode(Intent intent, boolean startupMode) {
		intent.putExtra(EXTRA_STARTUP_MODE, startupMode);
	}

	public static boolean isStartupMode(Intent intent, boolean defaultValue) {
		return intent.getBooleanExtra(EXTRA_STARTUP_MODE, defaultValue);
	}

	public static void putThirdPartyUser(Intent intent, ThirdPartyUser thirdPartyUser) {
		intent.putExtra(EXTRA_THIRD_PARTY_USER, thirdPartyUser);
	}

	public static ThirdPartyUser getThirdPartyUser(Intent intent) {
		return intent.getParcelableExtra(EXTRA_THIRD_PARTY_USER);
	}

	public static void putErrorMessage(Intent intent, String errorMessage) {
		intent.putExtra(EXTRA_ERROR_MESSAGE, errorMessage);
	}

	public static String getErrorMessage(Intent intent) {
		return intent.getStringExtra(EXTRA_ERROR_MESSAGE);
	}

	private IntentUtils() {
	}
}
