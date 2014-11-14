package com.citymaps.mobile.android.model;

import java.util.HashMap;
import java.util.Map;

public enum ThirdParty {
	FACEBOOK("facebook", "Facebook"),
	GOOGLE("google", "Google");

	private static Map<String, ThirdParty> mValueMap;

	private static ThirdParty fromString(String string) {
		if (mValueMap == null) {
			ThirdParty[] values = values();
			mValueMap = new HashMap<String, ThirdParty>(values.length);
			for (ThirdParty value : values) {
				mValueMap.put(value.mValue, value);
			}
		}
		return mValueMap.get(string.toLowerCase());
	}

	private String mValue;

	private String mProperName;

	private ThirdParty(String value, String properName) {
		mValue = value;
		mProperName = properName;
	}

	public String getProperName() {
		return mProperName;
	}

	@Override
	public String toString() {
		return mValue;
	}
}
