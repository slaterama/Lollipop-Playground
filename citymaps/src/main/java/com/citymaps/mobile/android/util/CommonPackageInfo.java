package com.citymaps.mobile.android.util;//

import java.util.HashMap;
import java.util.Map;

public enum CommonPackageInfo {
	ANDROID_MESSENGER("com.google.android.apps.messaging", 3000),
	EMAIL("com.android.email", 2200),
	FACEBOOK("com.facebook.katana", 1100),
	FACEBOOK_MESSENGER("com.facebook.orca", 3200),
	GMAIL("com.google.android.gm", 2100),
	GOOGLE_PLUS("com.google.android.apps.plus", 1000),
	HANGOUTS("com.google.android.talk", 3100),
	INBOX("com.google.android.apps.inbox", 2000),
	MMS("com.android.mms", 3300),
	TWITTER("com.twitter.android", 1200),
	UNKNOWN("", Integer.MAX_VALUE);

	private static Map<String, CommonPackageInfo> sPackageNameMap;

	public static CommonPackageInfo fromPackageName(String packageName) {
		if (sPackageNameMap == null) {
			CommonPackageInfo[] infos = values();
			sPackageNameMap = new HashMap<String, CommonPackageInfo>(infos.length);
			for (CommonPackageInfo info : infos) {
				sPackageNameMap.put(info.mPackageName, info);
			}
		}
		CommonPackageInfo info = sPackageNameMap.get(packageName);
		return (info == null ? UNKNOWN : info);
	}

	String mPackageName;
	int mSortCategory;

	private CommonPackageInfo(String packageName, int sortCategory) {
		mPackageName = packageName;
		mSortCategory = sortCategory;
	}

	public String getPackageName() {
		return mPackageName;
	}

	public int getSortCategory() {
		return mSortCategory;
	}
}
