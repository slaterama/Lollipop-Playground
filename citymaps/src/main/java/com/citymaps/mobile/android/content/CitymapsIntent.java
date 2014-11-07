package com.citymaps.mobile.android.content;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import com.citymaps.mobile.android.BuildConfig;
import com.citymaps.mobile.android.model.vo.Status;
import com.citymaps.mobile.android.model.vo.Config;

/**
 * A class for referencing Citymaps-specific Intent actions, categories and extras.
 */
public class CitymapsIntent extends Intent {

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

	private static final String PACKAGE_NAME = BuildConfig.class.getPackage().getName();

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

	public static void putConfig(Intent intent, Config config) {
		intent.putExtra(EXTRA_CONFIG, config);
	}

	public static Config getConfig(Intent intent) {
		return intent.getParcelableExtra(EXTRA_CONFIG);
	}

	public static void putApiStatus(Intent intent, Status status) {
		intent.putExtra(EXTRA_API_STATUS, status);
	}

	public static Status getApiStatus(Intent intent) {
		return intent.getParcelableExtra(EXTRA_API_STATUS);
	}

	/**
	 * Create an empty intent.
	 */
	public CitymapsIntent() {
		super();
	}

	/**
	 * Copy constructor.
	 */
	public CitymapsIntent(Intent o) {
		super(o);
	}

	/**
	 * Create an intent with a given action. All other fields (data, type, class) are null.
	 * Note that the action must be in a namespace because Intents are used globally in the system --
	 * for example the system VIEW action is android.intent.action.VIEW; an application's custom
	 * action would be something like com.google.app.myapp.CUSTOM_ACTION.
	 * @param action The Intent action, such as ACTION_VIEW.
	 */
	public CitymapsIntent(String action) {
		super(action);
	}

	/**
	 * <p>Create an intent with a given action and for a given data url. Note that the action must be
	 * in a namespace because Intents are used globally in the system -- for example the system VIEW
	 * action is android.intent.action.VIEW; an application's custom action would be something like
	 * com.google.app.myapp.CUSTOM_ACTION.</p>
	 * <p><i>Note: scheme and host name matching in the Android framework is case-sensitive, unlike
	 * the formal RFC. As a result, you should always ensure that you write your Uri with these
	 * elements using lower case letters, and normalize any Uris you receive from outside of Android
	 * to ensure the scheme and host is lower case.</i></p>
	 * @param action The Intent action, such as ACTION_VIEW.
	 * @param uri The Intent data URI.
	 */
	public CitymapsIntent(String action, Uri uri) {
		super(action, uri);
	}

	/**
	 * Create an intent for a specific component. All other fields (action, data, type, class) are
	 * null, though they can be modified later with explicit calls. This provides a convenient way
	 * to create an intent that is intended to execute a hard-coded class name, rather than relying
	 * on the system to find an appropriate class for you; see setComponent(ComponentName) for more
	 * information on the repercussions of this.
	 * @param packageContext A Context of the application package implementing this class.
	 * @param cls The component class that is to be used for the intent.
	 */
	public CitymapsIntent(Context packageContext, Class<?> cls) {
		super(packageContext, cls);
	}

	/**
	 * <p>Create an intent for a specific component with a specified action and data. This is equivalent
	 * using Intent(String, android.net.Uri) to construct the Intent and then calling setClass(Context, Class)
	 * to set its class.</p>
	 * <p><i>Note: scheme and host name matching in the Android framework is case-sensitive, unlike
	 * the formal RFC. As a result, you should always ensure that you write your Uri with these elements
	 * using lower case letters, and normalize any Uris you receive from outside of Android to ensure
	 * the scheme and host is lower case.</i></p>
	 * @param action The Intent action, such as ACTION_VIEW.
	 * @param uri The Intent data URI.
	 * @param packageContext A Context of the application package implementing this class.
	 * @param cls The component class that is to be used for the intent.
	 */
	public CitymapsIntent(String action, Uri uri, Context packageContext, Class<?> cls) {
		super(action, uri, packageContext, cls);
	}
}
