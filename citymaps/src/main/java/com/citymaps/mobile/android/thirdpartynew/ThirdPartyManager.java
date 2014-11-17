package com.citymaps.mobile.android.thirdpartynew;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import com.citymaps.mobile.android.thirdpartynew.ThirdParty.ConnectionCallbacks;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class ThirdPartyManager {

	private Activity mActivity;

	private Map<ThirdParty.Type, ThirdParty> mThirdPartyMap;

	private ConnectionCallbacks mConnectionCallbacks;

	public ThirdPartyManager(Activity activity, ConnectionCallbacks callbacks) {
		super();
		mActivity = activity;
		mConnectionCallbacks = callbacks;
		mThirdPartyMap = new LinkedHashMap<ThirdParty.Type, ThirdParty>(ThirdParty.Type.values().length);
	}

	public Activity getActivity() {
		return mActivity;
	}

	public ConnectionCallbacks getCallbacks() {
		return mConnectionCallbacks;
	}

	public ThirdParty add(ThirdParty.Type type) {
		ThirdParty thirdParty = mThirdPartyMap.get(type);
		if (thirdParty == null) {
			thirdParty = ThirdParty.newInstance(type, mActivity, mConnectionCallbacks);
			mThirdPartyMap.put(type, thirdParty);
		}
		return thirdParty;
	}

	public ThirdParty remove(ThirdParty.Type type) {
		return mThirdPartyMap.remove(type);
	}

	public ThirdParty get(ThirdParty.Type type) {
		return mThirdPartyMap.get(type);
	}
	
	/* Lifecycle methods */

	public void onCreate(Bundle savedInstanceState) {
		Collection<ThirdParty> values = mThirdPartyMap.values();
		for (ThirdParty connection : values) {
			connection.callOnCreate(savedInstanceState);
		}
	}

	public void onResume() {
		Collection<ThirdParty> values = mThirdPartyMap.values();
		for (ThirdParty connection : values) {
			connection.callOnResume();
		}
	}

	public void onStart() {
		Collection<ThirdParty> values = mThirdPartyMap.values();
		for (ThirdParty connection : values) {
			connection.callOnStart();
		}
	}

	public void onSaveInstanceState(@NonNull Bundle outState) {
		Collection<ThirdParty> values = mThirdPartyMap.values();
		for (ThirdParty connection : values) {
			connection.callOnSaveInstanceState(outState);
		}
	}

	public void onPause() {
		Collection<ThirdParty> values = mThirdPartyMap.values();
		for (ThirdParty connection : values) {
			connection.callOnPause();
		}
	}

	public void onStop() {
		Collection<ThirdParty> values = mThirdPartyMap.values();
		for (ThirdParty connection : values) {
			connection.callOnStop();
		}
	}

	public void onDestroy() {
		Collection<ThirdParty> values = mThirdPartyMap.values();
		for (ThirdParty connection : values) {
			connection.callOnDestroy();
		}
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		Collection<ThirdParty> values = mThirdPartyMap.values();
		for (ThirdParty connection : values) {
			connection.callOnActivityResult(requestCode, resultCode, data);
		}
	}
}
