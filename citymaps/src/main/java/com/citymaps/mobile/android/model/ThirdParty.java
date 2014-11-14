package com.citymaps.mobile.android.model;

import java.util.HashMap;
import java.util.Map;

public enum ThirdParty {
	FACEBOOK("facebook", "Facebook", 1),
	GOOGLE("google", "Google", 2);

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

	private int mAvatarProvider;

	private ThirdParty(String value, String properName, int avatarProvider) {
		mValue = value;
		mProperName = properName;
		mAvatarProvider = avatarProvider;
	}

	public String getProperName() {
		return mProperName;
	}

	public int getAvatarProvider() {
		return mAvatarProvider;
	}

	@Override
	public String toString() {
		return mValue;
	}
}
