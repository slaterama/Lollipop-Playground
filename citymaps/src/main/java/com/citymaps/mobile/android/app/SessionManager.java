//  This singleton implements the "Parametric initialization-on-demand holder idiom" for singletons
//  with immutable parameters in Java (in this case, the application context) as described here:
//  http://unafbapune.blogspot.com/2007/09/parametric-initialization-on-demand.html

package com.citymaps.mobile.android.app;

import android.content.Context;
import com.citymaps.mobile.android.config.Api;
import com.citymaps.mobile.android.config.Environment;

public class SessionManager {

	private static Context sContext;

	public static synchronized SessionManager getInstance(Context context) {
		sContext = context.getApplicationContext();
		return LazyHolder.INSTANCE;
	}

	private static final class LazyHolder {
		private static final SessionManager INSTANCE = new SessionManager();
	}

	public static Environment getEnvironment(Context context) {
		return getInstance(context).getEnvironment();
	}

	private Environment mEnvironment;

	private SessionManager() {
		mEnvironment = Environment.newInstance(sContext);
	}

	public Context getContext() {
		return sContext;
	}

	public Environment getEnvironment() {
		return mEnvironment;
	}

	public Api registerVersion(int version, String build) {
		return mEnvironment.registerVersion(version, build);
	}
}
