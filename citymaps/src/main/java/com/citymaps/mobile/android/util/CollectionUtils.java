package com.citymaps.mobile.android.util;

import java.util.Iterator;
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

	/**
	 * A not-very-effecient way of getting the last entry from a map.
	 * @param map
	 * @param <K>
	 * @param <V>
	 * @return
	 */
	public static <K, V> Map.Entry<K, V> getLastEntry(Map<K, V> map) {
		try {
			Map.Entry<K, V> entry = null;
			Iterator<Map.Entry<K, V>> iterator = map.entrySet().iterator();
			while (iterator.hasNext()) {
				entry = iterator.next();
			}
			return entry;
		} catch (NullPointerException e) {
			return null;
		} catch (NoSuchElementException e) {
			return null;
		}
	}

	public static <K, V> K getLastKey(Map<K, V> map) {
		try {
			return getLastEntry(map).getKey();
		} catch (NullPointerException e) {
			return null;
		}
	}

	public static <K, V> V getLastValue(Map<K, V> map) {
		try {
			return getLastEntry(map).getValue();
		} catch (NullPointerException e) {
			return null;
		}
	}

	private CollectionUtils() {
	}
}
