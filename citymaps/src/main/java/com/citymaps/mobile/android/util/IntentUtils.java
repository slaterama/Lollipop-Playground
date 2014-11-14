package com.citymaps.mobile.android.util;

import android.content.Intent;
import com.citymaps.mobile.android.BuildConfig;
import com.citymaps.mobile.android.model.Config;
import com.citymaps.mobile.android.model.ThirdParty;
import com.facebook.Session;
import com.facebook.model.GraphUser;

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

	public static final String EXTRA_THIRD_PARTY = makeExtra("THIRD_PARTY");
	public static final String EXTRA_THIRD_PARTY_ID = makeExtra("THIRD_PARTY_ID");
	public static final String EXTRA_THIRD_PARTY_TOKEN = makeExtra("THIRD_PARTY_TOKEN");
	public static final String EXTRA_THIRD_PARTY_FIRST_NAME = makeExtra("THIRD_PARTY_FIRST_NAME");
	public static final String EXTRA_THIRD_PARTY_LAST_NAME = makeExtra("THIRD_PARTY_LAST_NAME");
	public static final String EXTRA_THIRD_PARTY_USERNAME = makeExtra("THIRD_PARTY_USERNAME");
	public static final String EXTRA_THIRD_PARTY_EMAIL = makeExtra("THIRD_PARTY_EMAIL");
	public static final String EXTRA_THIRD_PARTY_AVATAR_URL = makeExtra("THIRD_PARTY_AVATAR_URL");

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

	public static void putThirdPartyUser(Intent intent, Session session, GraphUser user) {
		if (session == null || user == null) {
			return;
		}

		intent.putExtra(EXTRA_THIRD_PARTY, ThirdParty.FACEBOOK);
		intent.putExtra(EXTRA_THIRD_PARTY_ID, user.getId());
		intent.putExtra(EXTRA_THIRD_PARTY_TOKEN, session.getAccessToken());
		intent.putExtra(EXTRA_THIRD_PARTY_FIRST_NAME, user.getFirstName());
		intent.putExtra(EXTRA_THIRD_PARTY_LAST_NAME, user.getLastName());
		intent.putExtra(EXTRA_THIRD_PARTY_USERNAME, user.getUsername());
		Object email = user.getProperty(PROPERTY_NAME_EMAIL);
		if (email != null) {
			intent.putExtra(EXTRA_THIRD_PARTY_EMAIL, email.toString());
		}
		intent.putExtra(EXTRA_THIRD_PARTY_AVATAR_URL, FacebookUtils.getAvatarUrl(user.getId(), FacebookUtils.PictureType.LARGE, true));
	}

	public static ThirdParty getThirdParty(Intent intent) {
		return (ThirdParty) intent.getSerializableExtra(EXTRA_THIRD_PARTY);
	}

	public static String getThirdPartyId(Intent intent) {
		return intent.getStringExtra(EXTRA_THIRD_PARTY_ID);
	}

	public static String getThirdPartyToken(Intent intent) {
		return intent.getStringExtra(EXTRA_THIRD_PARTY_TOKEN);
	}

	public static String getThirdPartyFirstName(Intent intent) {
		return intent.getStringExtra(EXTRA_THIRD_PARTY_FIRST_NAME);
	}

	public static String getThirdPartyLastName(Intent intent) {
		return intent.getStringExtra(EXTRA_THIRD_PARTY_LAST_NAME);
	}

	public static String getThirdPartyUsername(Intent intent) {
		return intent.getStringExtra(EXTRA_THIRD_PARTY_USERNAME);
	}

	public static String getThirdPartyEmail(Intent intent) {
		return intent.getStringExtra(EXTRA_THIRD_PARTY_EMAIL);
	}

	public static String getThirdPartyAvatarUrl(Intent intent) {
		return intent.getStringExtra(EXTRA_THIRD_PARTY_AVATAR_URL);
	}

	private IntentUtils() {
	}
}
