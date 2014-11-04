package com.citymaps.mobile.android.util;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import com.citymaps.mobile.android.os.BuildVersion;

public class PackageUtils {

	private static final String BASE_API_VERSION_NUMBER = "com.citymaps.mobile.android.BASE_API_VERSION_NUMBER";
	private static final String BASE_API_BUILD_STRING = "com.citymaps.mobile.android.BASE_API_BUILD_STRING";

	public static Object getMetaData(Context context, String key) {
		try {
			ApplicationInfo info = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
			return info.metaData.get(key);
		} catch (PackageManager.NameNotFoundException e) {
			return null;
		}
	}

	public static int getBaseApiVersionNumber(Context context) {
		return (Integer) getMetaData(context, BASE_API_VERSION_NUMBER);
	}

	public static String getBaseApiBuildString(Context context) {
		return (String) getMetaData(context, BASE_API_BUILD_STRING);
	}

	public static BuildVersion getBaseApiBuildVersion(Context context) {
		return new BuildVersion(getBaseApiBuildString(context));
	}

	public static int getVersionCode(Context context) {
		try {
			PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			return info.versionCode;
		} catch (PackageManager.NameNotFoundException e) {
			return -1;
		}
	}

	public static String getVersionName(Context context) {
		try {
			PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			return info.versionName;
		} catch (PackageManager.NameNotFoundException e) {
			return null;
		}
	}

	public static BuildVersion getVersion(Context context) {
		try {
			PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			return new BuildVersion(info.versionName);
		} catch (PackageManager.NameNotFoundException e) {
			return null;
		}
	}
}
