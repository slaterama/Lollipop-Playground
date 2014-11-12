package com.citymaps.mobile.android.model;

import java.util.HashMap;
import java.util.Map;

public enum ThirdParty {
	FACEBOOK("facebook"),
	GOOGLE("google");

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

	private ThirdParty(String value) {
		mValue = value;
	}

	@Override
	public String toString() {
		return mValue;
	}
}
