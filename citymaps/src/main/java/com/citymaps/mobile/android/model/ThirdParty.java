package com.citymaps.mobile.android.model;

import com.citymaps.mobile.android.util.Pref;

import java.util.HashMap;
import java.util.Map;

public enum ThirdParty {
	FACEBOOK("facebook", "Facebook", Pref.FACEBOOK_TOKEN),
	GOOGLE("google", "Google", Pref.GOOGLE_TOKEN);

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

	private Pref mPreference;

	private ThirdParty(String value, String properName, Pref preference) {
		mValue = value;
		mProperName = properName;
		mPreference = preference;
	}

	public String getProperName() {
		return mProperName;
	}

	public Pref getPreference() {
		return mPreference;
	}

	@Override
	public String toString() {
		return mValue;
	}
}
