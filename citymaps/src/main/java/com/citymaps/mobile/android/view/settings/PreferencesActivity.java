package com.citymaps.mobile.android.view.settings;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.Menu;
import android.view.MenuItem;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.citymaps.mobile.android.R;
import com.citymaps.mobile.android.app.SessionManager;
import com.citymaps.mobile.android.app.TrackedActionBarActivity;
import com.citymaps.mobile.android.app.VolleyManager;
import com.citymaps.mobile.android.model.User;
import com.citymaps.mobile.android.model.volley.UserRequest;
import com.citymaps.mobile.android.view.MainActivity;
import com.citymaps.mobile.android.view.housekeeping.SignoutDialogFragment;

public class PreferencesActivity extends TrackedActionBarActivity
		implements MainPreferencesFragment.MainPreferencesListener,
		SignoutDialogFragment.OnSignoutListener {

	public static final int DEFAULT_PREFERENCES = 0;
	public static final int DEVELOPER_PREFERENCES = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (savedInstanceState == null) {
			Fragment fragment = MainPreferencesFragment.newInstance();
			getSupportFragmentManager()
					.beginTransaction()
					.add(android.R.id.content, fragment, null)
					.commit();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.preferences, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onReceiveEmailNotificationsChange(User user, boolean notifications) {

	}

	@Override
	public void onSigninClick() {

	}

	@Override
	public void onSignoutClick() {
		FragmentManager manager = getSupportFragmentManager();
		if (manager.findFragmentByTag(SignoutDialogFragment.FRAGMENT_TAG) == null) {
			SignoutDialogFragment fragment = SignoutDialogFragment.newInstance();
			fragment.show(manager, SignoutDialogFragment.FRAGMENT_TAG);
		}
	}

	@Override
	public void onSignout() {
		final SessionManager sessionManager = SessionManager.getInstance(this);
		User currentUser = sessionManager.getCurrentUser();
		if (currentUser != null) {
			UserRequest request = UserRequest.newLogoutRequest(this, currentUser.getId(),
					new Response.Listener<User>() {
						@Override
						public void onResponse(User response) {
							sessionManager.setCurrentUser(null);
							setResult(MainActivity.RESULT_LOGOUT);
							finish();
						}
					},
					new Response.ErrorListener() {
						@Override
						public void onErrorResponse(VolleyError error) {
							// TODO Show error message
						}
					});
			VolleyManager.getInstance(this).getRequestQueue().add(request);
		}
	}
}
