package com.citymaps.mobile.android.map;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.view.View;
import android.view.ViewGroup;
import com.citymaps.citymapsengine.CitymapsMapView;
import com.citymaps.citymapsengine.options.CitymapsMapViewOptions;
import com.citymaps.mobile.android.util.ActivityUtils;
import com.citymaps.mobile.android.util.LogEx;
import com.citymaps.mobile.android.util.objectscompat.ObjectsCompat;
import com.citymaps.mobile.android.util.viewcompat.ViewCompat;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;

public class MapViewService extends Service {

	private MapViewBinder mMapViewBinder;

	private Map<ViewGroup, MapViewOwner> mContainerMap;

	private ViewGroup mCurrentContainer;

	private CitymapsMapView mMapView;

	private View.OnAttachStateChangeListener mOnAttachStateChangeListener = new View.OnAttachStateChangeListener() {
		@Override
		public void onViewAttachedToWindow(View v) {
			updateMapOwner();
		}

		@Override
		public void onViewDetachedFromWindow(View v) {
			updateMapOwner();
		}
	};

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		int retVal = super.onStartCommand(intent, flags, startId);
		if (LogEx.isLoggable(LogEx.VERBOSE)) {
			LogEx.v(String.format("intent=%s, flags=%d, startId=%d", intent, flags, startId));
		}
		return retVal;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mMapView != null) {
			mMapView.onDestroy();
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		if (mMapViewBinder == null) {
			mMapViewBinder = new MapViewBinder();
		}
		return mMapViewBinder;
	}

	private void updateMapOwner() {
		ViewGroup highestResumedContainer = null;
		int highestResumedContainerHeight = 0;

		ViewGroup highestStartedContainer = null;
		int highestStartedContainerHeight = 0;

		Set<ViewGroup> keySet = mContainerMap.keySet();
		for (ViewGroup viewGroup : keySet) {
			Activity activity = ViewCompat.getCurrentActivity(viewGroup);
			if (activity != null) {
				boolean isResumed = ActivityUtils.isResumed(activity);
				if (isResumed) {
					int height = ViewCompat.getHierarchyHeight(viewGroup);
					if (height > highestResumedContainerHeight) {
						highestResumedContainerHeight = height;
						highestResumedContainer = viewGroup;
					}
					continue;
				}

				if (highestResumedContainerHeight == 0) {
					boolean isStarted = ActivityUtils.isStarted(activity);
					if (isStarted) {
						int height = ViewCompat.getHierarchyHeight(viewGroup);
						if (height > highestStartedContainerHeight) {
							highestStartedContainerHeight = height;
							highestStartedContainer = viewGroup;
						}
					}
				}
			}
		}

		ViewGroup newContainer = (highestResumedContainer != null ? highestResumedContainer : highestStartedContainer);
		if (newContainer != null) {
			if (mMapView == null) {
				mMapView = new CitymapsMapView(this, new CitymapsMapViewOptions());
			}
		}

		if (!ObjectsCompat.equals(mCurrentContainer, newContainer)) {
			if (mCurrentContainer != null) {
				MapViewOwner owner = mContainerMap.get(mCurrentContainer);
				boolean removed = false;
				if (owner != null) {
					owner.onMapViewOwnershipRevoking(mCurrentContainer, mMapView);
					removed = owner.onRemoveMapView(mCurrentContainer, mMapView);
				}
				if (!removed && mMapView.getParent() == mCurrentContainer) {
					mCurrentContainer.removeView(mMapView);
					mMapView.onPause();
				}
				if (owner != null) {
					owner.onMapViewOwnershipRevoked(mCurrentContainer, mMapView);
				}
			}

			mCurrentContainer = newContainer;

			if (mCurrentContainer != null) {
				MapViewOwner owner = mContainerMap.get(mCurrentContainer);
				boolean added = false;
				if (owner != null) {
					added = owner.onAddMapView(mCurrentContainer, mMapView);
				}
				if (!added) {
					mCurrentContainer.addView(mMapView, 0);
					mMapView.onResume();
				}
				if (owner != null) {
					owner.onMapViewOwnershipGranted(mCurrentContainer, mMapView);
				}
			}
		}
	}

	public class MapViewBinder extends Binder {

		public void registerMapViewContainer(ViewGroup container, MapViewOwner owner) {
			if (container == null) {
				throw new IllegalArgumentException("container may not be null");
			}

			if (mContainerMap == null) {
				mContainerMap = new IdentityHashMap<ViewGroup, MapViewOwner>();
			}

			mContainerMap.put(container, owner);

			if (ViewCompat.isAttachedToWindow(container)) {
				updateMapOwner();
			}

			container.addOnAttachStateChangeListener(mOnAttachStateChangeListener);
		}

		public void unregisterMapViewContainer(ViewGroup container) {
			if (mOnAttachStateChangeListener != null) {
				container.removeOnAttachStateChangeListener(mOnAttachStateChangeListener);
			}

			if (mContainerMap != null) {
				mContainerMap.remove(container);
			}

			updateMapOwner();
		}

		public CitymapsMapView getMapView() {
			return mMapView;
		}
	}
}
