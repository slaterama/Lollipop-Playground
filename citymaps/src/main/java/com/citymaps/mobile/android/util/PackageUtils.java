package com.citymaps.mobile.android.util;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import com.citymaps.mobile.android.BuildConfig;
import com.citymaps.mobile.android.os.SoftwareVersion;

public class PackageUtils {

	public static final String CITYMAPS_SECRET = "com.citymaps.mobile.android.CITYMAPS_SECRET";
	public static final String BASE_API_VERSION_NUMBER = "com.citymaps.mobile.android.BASE_API_VERSION_NUMBER";
	public static final String BASE_API_BUILD_STRING = "com.citymaps.mobile.android.BASE_API_BUILD_STRING";

	public static Object getMetaData(Context context, String key, Object fallback) {
		try {
			ApplicationInfo info = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
			return info.metaData.get(key);
		} catch (PackageManager.NameNotFoundException e) {
			return fallback;
		}
	}

	public static String getCitymapsSecret(Context context, String fallback) {
		return (String) getMetaData(context, CITYMAPS_SECRET, fallback);
	}

	public static String getCitymapsSecret(Context context) {
		return getCitymapsSecret(context, null);
	}

	public static int getBaseApiVersion(Context context, int fallback) {
		return (Integer) getMetaData(context, BASE_API_VERSION_NUMBER, fallback);
	}

	public static int getBaseApiVersion(Context context) {
		return getBaseApiVersion(context, -1);
	}

	public static String getBaseApiBuildString(Context context, String fallback) {
		return (String) getMetaData(context, BASE_API_BUILD_STRING, fallback);
	}

	public static String getBaseApiBuildString(Context context) {
		return getBaseApiBuildString(context, null);
	}

	public static SoftwareVersion getBaseApiBuild(Context context, SoftwareVersion fallback) {
		return new SoftwareVersion((String) getMetaData(context, BASE_API_BUILD_STRING, fallback));
	}

	/*
	public static int getAppVersionCode(Context context, int fallback) {
		try {
			PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			return info.versionCode;
		} catch (PackageManager.NameNotFoundException e) {
			return fallback;
		}
	}

	public static int getAppVersionCode(Context context) {
		return getAppVersionCode(context, -1);
	}

	public static String getAppVersionName(Context context, String fallback) {
		try {
			PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			return info.versionName;
		} catch (PackageManager.NameNotFoundException e) {
			return fallback;
		}
	}

	public static String getAppVersionName(Context context) {
		return getAppVersionName(context, null);
	}
	*/

	public static SoftwareVersion getAppVersion() {
		return new SoftwareVersion(BuildConfig.VERSION_NAME);
	}

	private PackageUtils() {
	}
}
