//  This singleton implements the "Parametric initialization-on-demand holder idiom" for singletons
//  with immutable parameters in Java (in this case, the application context) as described here:
//  http://unafbapune.blogspot.com/2007/09/parametric-initialization-on-demand.html

package com.citymaps.mobile.android.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import com.citymaps.mobile.android.BuildConfig;
import com.citymaps.mobile.android.config.Environment;
import com.citymaps.mobile.android.model.vo.Config;
import com.citymaps.mobile.android.util.SharedPreferenceUtils;
import com.citymaps.mobile.android.view.HardUpdateActivity;

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

	private SessionManager() {
		mEnvironment = Environment.newInstance(sContext);
		mConfig = SharedPreferenceUtils.getConfig(sContext);
	}

	public Config getConfig() {
		return mConfig;
	}

	public Context getContext() {
		return sContext;
	}

	public Environment getEnvironment() {
		return mEnvironment;
	}
}
