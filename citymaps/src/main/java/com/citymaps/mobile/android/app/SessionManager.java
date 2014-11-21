//  This singleton implements the "Parametric initialization-on-demand holder idiom" for singletons
//  with immutable parameters in Java (in this case, the application context) as described here:
//  http://unafbapune.blogspot.com/2007/09/parametric-initialization-on-demand.html

package com.citymaps.mobile.android.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.citymaps.mobile.android.config.Environment;
import com.citymaps.mobile.android.model.Config;
import com.citymaps.mobile.android.model.User;
import com.citymaps.mobile.android.model.UserSettings;
import com.citymaps.mobile.android.util.SharedPreferenceUtils;

public class SessionManager {

	private static Context sContext;

	public static synchronized SessionManager getInstance(Context context) {
		sContext = context.getApplicationContext();
		return LazyHolder.INSTANCE;
	}

	private static final class LazyHolder {
		private static final SessionManager INSTANCE = new SessionManager();
	}

	private static final String CONFIG_PREFERENCES_SUFFIX = "configPreferences";

	private static SharedPreferences getConfigPreferences(Context context) {
		String name = String.format("%s_%s", context.getPackageName(), CONFIG_PREFERENCES_SUFFIX);
		return context.getSharedPreferences(name, Context.MODE_PRIVATE);
	}

	private Environment mEnvironment;

	private Config mConfig;

	private User mCurrentUser;
	private UserSettings mCurrentUserSettings;

	private SessionManager() {
		mEnvironment = Environment.newInstance(sContext);
		SharedPreferences sp = SharedPreferenceUtils.getConfigSharedPreferences(sContext);
		mConfig = SharedPreferenceUtils.getConfig(sp);
	}

	public Config getConfig() {
		return mConfig;
	}

	public Context getContext() {
		return sContext;
	}

	public User getCurrentUser() {
		return mCurrentUser;
	}

	public void setCurrentUser(User user) {
		if (user != mCurrentUser) {
			mCurrentUser = user;

			setCurrentUserSettings(null);

			SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(sContext);
			if (user == null) {
				SharedPreferenceUtils.remove(sp, SharedPreferenceUtils.Key.CITYMAPS_TOKEN).apply();
				mCurrentUserSettings = null;
			} else {
				SharedPreferenceUtils.putCitymapsToken(sp, user.getCitymapsToken()).apply();
			}

			// TODO Send broadcast?
		}
	}

	public UserSettings getCurrentUserSettings() {
		return mCurrentUserSettings;
	}

	public void setCurrentUserSettings(UserSettings settings) {
		if (settings != mCurrentUserSettings) {
			mCurrentUserSettings = settings;
		}
	}

	public Environment getEnvironment() {
		return mEnvironment;
	}
}
