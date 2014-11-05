package com.citymaps.mobile.android.util;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import com.citymaps.mobile.android.os.BuildVersion;

public class PackageUtils {

	private static final String BASE_API_VERSION_NUMBER = "com.citymaps.mobile.android.BASE_API_VERSION_NUMBER";
	private static final String BASE_API_BUILD_STRING = "com.citymaps.mobile.android.BASE_API_BUILD_STRING";

	public static Object getMetaData(Context context, String key) throws PackageManager.NameNotFoundException {
		ApplicationInfo info = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
		return info.metaData.get(key);
	}

	public static int getBaseApiVersionNumber(Context context) throws PackageManager.NameNotFoundException {
		return (Integer) getMetaData(context, BASE_API_VERSION_NUMBER);
	}

	public static String getBaseApiBuildString(Context context) throws PackageManager.NameNotFoundException {
		return (String) getMetaData(context, BASE_API_BUILD_STRING);
	}

	public static BuildVersion getBaseApiBuildVersion(Context context) throws PackageManager.NameNotFoundException {
		return new BuildVersion(getBaseApiBuildString(context));
	}

	public static int getAppVersionCode(Context context) throws PackageManager.NameNotFoundException {
		PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
		return info.versionCode;
	}

	public static String getAppVersionName(Context context) throws PackageManager.NameNotFoundException {
		PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
		return info.versionName;
	}

	public static BuildVersion getAppBuildVersion(Context context) throws PackageManager.NameNotFoundException {
		PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
		return new BuildVersion(info.versionName);
	}

	private PackageUtils() {
	}
}
