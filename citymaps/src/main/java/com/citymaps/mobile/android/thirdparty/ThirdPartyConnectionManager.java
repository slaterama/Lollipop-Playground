package com.citymaps.mobile.android.thirdparty;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import java.util.*;

public class ThirdPartyConnectionManager {

	private Activity mActivity;

	private Map<ThirdParty, ThirdPartyConnection> mConnectionMap;

	public ThirdPartyConnectionManager(Activity activity) {
		mActivity = activity;
		mConnectionMap = new LinkedHashMap<ThirdParty, ThirdPartyConnection>();
	}

	public void onCreate(Bundle savedInstanceState) {
		Collection<ThirdPartyConnection> values = mConnectionMap.values();
		for (ThirdPartyConnection connection : values) {
			connection.onCreate(savedInstanceState);
		}
	}

	public void onResume() {
		Collection<ThirdPartyConnection> values = mConnectionMap.values();
		for (ThirdPartyConnection connection : values) {
			connection.onResume();
		}
	}

	public void onStart() {
		Collection<ThirdPartyConnection> values = mConnectionMap.values();
		for (ThirdPartyConnection connection : values) {
			connection.onStart();
		}
	}

	public void onSaveInstanceState(@NonNull Bundle outState) {
		Collection<ThirdPartyConnection> values = mConnectionMap.values();
		for (ThirdPartyConnection connection : values) {
			connection.onSaveInstanceState(outState);
		}
	}

	public void onPause() {
		Collection<ThirdPartyConnection> values = mConnectionMap.values();
		for (ThirdPartyConnection connection : values) {
			connection.onPause();
		}
	}

	public void onStop() {
		Collection<ThirdPartyConnection> values = mConnectionMap.values();
		for (ThirdPartyConnection connection : values) {
			connection.onStop();
		}
	}

	public void onDestroy() {
		Collection<ThirdPartyConnection> values = mConnectionMap.values();
		for (ThirdPartyConnection connection : values) {
			connection.onDestroy();
		}
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		Collection<ThirdPartyConnection> values = mConnectionMap.values();
		for (ThirdPartyConnection connection : values) {
			connection.onActivityResult(requestCode, resultCode, data);
		}
	}

	public ThirdPartyConnection addConnection(ThirdParty thirdParty) {
		ThirdPartyConnection connection = null;
		if (thirdParty != null) {
			connection = mConnectionMap.get(thirdParty);
			if (connection == null) {
				connection = ThirdPartyConnection.newInstance(thirdParty, mActivity);
				mConnectionMap.put(thirdParty, connection);
			}
		}
		return connection;
	}

	public ThirdPartyConnection getConnection(ThirdParty thirdParty) {
		return mConnectionMap.get(thirdParty);
	}

	public void removeConnection(ThirdParty thirdParty) {
		ThirdPartyConnection connection = mConnectionMap.remove(thirdParty);
		if (connection != null) {
			connection.disconnect();
		}
	}

	public void removeAll() {
		Collection<ThirdPartyConnection> values = mConnectionMap.values();
		Iterator<ThirdPartyConnection> iterator = values.iterator();
		while(iterator.hasNext()) {
			ThirdPartyConnection connection = iterator.next();
			connection.disconnect();
			iterator.remove();
		}
	}
}
