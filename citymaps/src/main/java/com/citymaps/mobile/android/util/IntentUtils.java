package com.citymaps.mobile.android.util;

import android.content.Intent;
import android.net.Uri;
import com.citymaps.mobile.android.map.ParcelableLonLat;
import com.citymaps.mobile.android.model.Config;
import com.citymaps.mobile.android.model.ThirdPartyUser;
import com.citymaps.mobile.android.view.cards.CardType;

/**
 * A class for referencing Citymaps-specific Intent actions, categories and extras.
 */
public class IntentUtils {

	private static final String URI_SCHEME = "content";
	private static final String URI_AUTHORITY = PackageUtils.getNonDevPackageName();
	private static final String URI_PATH_COLLECTION = "collection";
	private static final String URI_PATH_PLACE = "place";
	private static final String URI_PATH_USER = "user";

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
	private static final String PACKAGE_NAME = PackageUtils.getNonDevPackageName();

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

	public static final String EXTRA_CARD_TYPE = makeExtra("CARD_TYPE");

	/**
	 * BroadcastReceiver Action: Sent to inform about Android config information being loaded.
	 */
	public static final String ACTION_CONFIG_LOADED = makeAction("ACTION_LOADED");

	/**
	 * The current Citymaps config information.
	 */
	public static final String EXTRA_CONFIG = makeExtra("CONFIG");

	public static final String EXTRA_DEVELOPER_PASSWORD_JUST_ENTERED = makeExtra("DEVELOPER_PASSWORD_JUST_ENTERED");

	public static final String EXTRA_ERROR_MESSAGE = makeExtra("ERROR_MESSAGE");

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

	public static final String EXTRA_MAP_LOCATION = makeExtra("MAP_LOCATION");

	public static final String EXTRA_MAP_RADIUS = makeExtra("MAP_RADIUS");

	public static final String EXTRA_MAP_ZOOM = makeExtra("MAP_ZOOM");

	public static final String EXTRA_SIZE = makeExtra("SIZE");

	/**
	 * Whether the app is currently in "startup mode" (i.e. walking the user through
	 * a series of initial screens including LaunchActivity, TourActivity, etc.)
	 */
	public static final String EXTRA_STARTUP_MODE = makeExtra("STARTUP_MODE");

	public static final String EXTRA_THIRD_PARTY_USER = makeExtra("THIRD_PARTY_USER");

	public static void putCardType(Intent intent, CardType cardType) {
		intent.putExtra(EXTRA_CARD_TYPE, cardType);
	}

	public static CardType getCardType(Intent intent) {
		return (CardType) intent.getSerializableExtra(EXTRA_CARD_TYPE);
	}

	public static void putConfig(Intent intent, Config config) {
		intent.putExtra(EXTRA_CONFIG, config);
	}

	public static Config getConfig(Intent intent) {
		return intent.getParcelableExtra(EXTRA_CONFIG);
	}

	public static void putErrorMessage(Intent intent, String errorMessage) {
		intent.putExtra(EXTRA_ERROR_MESSAGE, errorMessage);
	}

	public static String getErrorMessage(Intent intent) {
		return intent.getStringExtra(EXTRA_ERROR_MESSAGE);
	}

	public static void putLoginMode(Intent intent, int loginMode) {
		intent.putExtra(EXTRA_LOGIN_MODE, loginMode);
	}

	public static int getLoginMode(Intent intent, int defaultValue) {
		return intent.getIntExtra(EXTRA_LOGIN_MODE, defaultValue);
	}

	public static void putMapLocation(Intent intent, ParcelableLonLat location) {
		intent.putExtra(EXTRA_MAP_LOCATION, location);
	}

	public static ParcelableLonLat getMapLocation(Intent intent) {
		return intent.getParcelableExtra(EXTRA_MAP_LOCATION);
	}

	public static void putMapRadius(Intent intent, float radius) {
		intent.putExtra(EXTRA_MAP_RADIUS, radius);
	}

	public static float getMapRadius(Intent intent, float defaultValue) {
		return intent.getFloatExtra(EXTRA_MAP_RADIUS, defaultValue);
	}

	public static void putMapZoom(Intent intent, int zoom) {
		intent.putExtra(EXTRA_MAP_ZOOM, zoom);
	}

	public static int getMapZoom(Intent intent, int defaultValue) {
		return intent.getIntExtra(EXTRA_MAP_ZOOM, defaultValue);
	}

	public static void putSize(Intent intent, int size) {
		intent.putExtra(EXTRA_SIZE, size);
	}

	public static int getSize(Intent intent, int defaultValue) {
		return intent.getIntExtra(EXTRA_SIZE, defaultValue);
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

	// Get Citymaps-specific intents (i.e. for deep-linking)

	public static Intent getCollectionIntent(String mapId) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.addCategory(Intent.CATEGORY_DEFAULT);
		Uri uri = new Uri.Builder()
				.scheme(URI_SCHEME)
				.encodedAuthority(URI_AUTHORITY)
				.appendEncodedPath(URI_PATH_COLLECTION)
				.appendEncodedPath(mapId)
				.build();
		intent.setData(uri);
		return intent;
	}

	public static Intent getPlaceIntent(String businessId) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.addCategory(Intent.CATEGORY_DEFAULT);
		Uri uri = new Uri.Builder()
				.scheme(URI_SCHEME)
				.encodedAuthority(URI_AUTHORITY)
				.appendEncodedPath(URI_PATH_PLACE)
				.appendEncodedPath(businessId)
				.build();
		intent.setData(uri);
		return intent;
	}

	public static Intent getUserIntent(String userId) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.addCategory(Intent.CATEGORY_DEFAULT);
		Uri uri = new Uri.Builder()
				.scheme(URI_SCHEME)
				.encodedAuthority(URI_AUTHORITY)
				.appendEncodedPath(URI_PATH_USER)
				.appendEncodedPath(userId)
				.build();
		intent.setData(uri);
		return intent;
	}

	private IntentUtils() {
	}
}
