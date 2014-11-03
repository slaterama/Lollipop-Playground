package com.citymaps.mobile.android.map;

import android.view.ViewGroup;
import com.citymaps.citymapsengine.CitymapsMapView;

public interface MapViewOwner {
	public void onMapViewOwnershipGranted(ViewGroup parent, CitymapsMapView mapView);
	public void onMapViewOwnershipRevoking(ViewGroup parent, CitymapsMapView mapView);
	public void onMapViewOwnershipRevoked(ViewGroup parent, CitymapsMapView mapView);
	public boolean onAddMapView(ViewGroup parent, CitymapsMapView mapView);
	public boolean onRemoveMapView(ViewGroup parent, CitymapsMapView mapView);
}
