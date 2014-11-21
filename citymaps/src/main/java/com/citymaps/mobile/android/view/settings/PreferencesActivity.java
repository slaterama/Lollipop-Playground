package com.citymaps.mobile.android.view.settings;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.citymaps.mobile.android.BuildConfig;
import com.citymaps.mobile.android.app.SessionManager;
import com.citymaps.mobile.android.app.TrackedActionBarActivity;
import com.citymaps.mobile.android.app.VolleyManager;
import com.citymaps.mobile.android.model.User;
import com.citymaps.mobile.android.model.volley.UserRequest;
import com.citymaps.mobile.android.util.ShareUtils;
import com.citymaps.mobile.android.view.MainActivity;
import com.citymaps.mobile.android.view.housekeeping.SignoutDialogFragment;

public class PreferencesActivity extends TrackedActionBarActivity
		implements MainPreferencesFragment.MainPreferencesListener,
		SignoutDialogFragment.OnSignoutListener {

	private static final String EMAIL_INTENT_TYPE = "message/rfc822";

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
	public void onShareAppClick() {
		ShareUtils.shareApp(this);
	}

	@Override
	public void onFeedbackClick() {
		User currentUser = SessionManager.getInstance(this).getCurrentUser();
		String subject;
		if (currentUser == null) {
			subject = BuildConfig.FEEDBACK_SUBJECT_VISITOR;
		} else {
			String fullName = currentUser.getFullName();
			if (TextUtils.isEmpty(fullName)) {
				subject = BuildConfig.FEEDBACK_SUBJECT_USER;
			} else {
				subject = String.format(BuildConfig.FEEDBACK_SUBJECT, fullName);
			}
		}
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType(EMAIL_INTENT_TYPE);
		intent.putExtra(Intent.EXTRA_EMAIL, BuildConfig.FEEDBACK_EMAILS);
		intent.putExtra(Intent.EXTRA_SUBJECT, subject);
		intent.putExtra(Intent.EXTRA_TEXT, "");
		startActivity(intent);
	}

	@Override
	public void onReceiveEmailNotificationsChange(boolean notifications) {

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
