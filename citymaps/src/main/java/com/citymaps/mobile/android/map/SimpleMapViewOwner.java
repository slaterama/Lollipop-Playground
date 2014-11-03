package com.citymaps.mobile.android.map;

import android.view.ViewGroup;
import com.citymaps.citymapsengine.CitymapsMapView;

public class SimpleMapViewOwner implements MapViewOwner {
	@Override
	public void onMapViewOwnershipGranted(ViewGroup parent, CitymapsMapView mapView) {
	}

	@Override
	public void onMapViewOwnershipRevoking(ViewGroup parent, CitymapsMapView mapView) {
	}

	@Override
	public void onMapViewOwnershipRevoked(ViewGroup parent, CitymapsMapView mapView) {
	}

	@Override
	public boolean onAddMapView(ViewGroup parent, CitymapsMapView mapView) {
		return false;
	}

	@Override
	public boolean onRemoveMapView(ViewGroup parent, CitymapsMapView mapView) {
		return false;
	}
}
