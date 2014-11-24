package com.citymaps.mobile.android.model;

import com.citymaps.mobile.android.util.SharedPreferenceUtils.Key;

import java.util.HashMap;
import java.util.Map;

public enum ThirdParty {
	FACEBOOK("facebook", "Facebook", Key.FACEBOOK_TOKEN),
	GOOGLE("google", "Google", Key.GOOGLE_TOKEN);

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

	private Key mSharedPreferenceTokenKey;

	private ThirdParty(String value, String properName, Key sharedPreferenceTokenKey) {
		mValue = value;
		mProperName = properName;
		mSharedPreferenceTokenKey = sharedPreferenceTokenKey;
	}

	public String getProperName() {
		return mProperName;
	}

	public Key getSharedPreferenceTokenKey() {
		return mSharedPreferenceTokenKey;
	}

	@Override
	public String toString() {
		return mValue;
	}
}
