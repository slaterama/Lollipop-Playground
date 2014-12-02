package com.citymaps.mobile.android.util;

import com.citymaps.citymapsengine.MapView;

public class MapUtils {

	public static final float DEFAULT_SEARCH_RADIUS = 15.0f;
	public static final int DEFAULT_SEARCH_ZOOM = 15;

	public static float getMapRadius(MapView mapView) {
		// See iOS CMMapController.mapRadius
		int size = Math.max(mapView.getWidth(), mapView.getHeight() / 2);
		return (float) mapView.getResolution() * size * 0.001f;
	}

	private MapUtils() {
	}
}
