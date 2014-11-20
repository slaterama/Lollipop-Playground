package com.citymaps.mobile.android.util;

import java.util.Map;
import java.util.NoSuchElementException;

public class CollectionUtils {

	public static <K, V> Map.Entry<K, V> getFirstEntry(Map<K, V> map) {
		try {
			return map.entrySet().iterator().next();
		} catch (NullPointerException e) {
			return null;
		} catch (NoSuchElementException e) {
			return null;
		}
	}

	public static <K, V> K getFirstKey(Map<K, V> map) {
		try {
			return getFirstEntry(map).getKey();
		} catch (NullPointerException e) {
			return null;
		}
	}

	public static <K, V> V getFirstValue(Map<K, V> map) {
		try {
			return getFirstEntry(map).getValue();
		} catch (NullPointerException e) {
			return null;
		}
	}

	private CollectionUtils() {
	}
}
