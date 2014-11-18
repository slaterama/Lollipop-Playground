package com.citymaps.mobile.android.util;

import android.net.Uri;
import android.text.TextUtils;

import java.util.Set;

public class UriUtils {

	public static String removeParameter(String uriString, String parameter) {
		Uri uri = Uri.parse(uriString);
		Uri.Builder builder = uri.buildUpon().clearQuery();
		Set<String> parameterNames = uri.getQueryParameterNames();
		for (String name : parameterNames) {
			if (!TextUtils.equals(name, parameter)) {
				builder.appendQueryParameter(name, uri.getQueryParameter(name));
			}
		}
		return builder.toString();
	}

	private UriUtils() {
	}
}
