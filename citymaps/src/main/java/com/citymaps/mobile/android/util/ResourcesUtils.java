package com.citymaps.mobile.android.util;

import android.content.res.Resources;
import android.util.TypedValue;

public class ResourcesUtils {

	public static float getFloat(Resources resources, int resId, float defaultValue) {
		try {
			TypedValue outValue = new TypedValue();
			resources.getValue(resId, outValue, true);
			return outValue.getFloat();
		} catch (Resources.NotFoundException e) {
			return defaultValue;
		}
	}

	private ResourcesUtils() {
	}
}
