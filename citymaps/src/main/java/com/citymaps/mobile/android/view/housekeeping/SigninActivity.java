package com.citymaps.mobile.android.view.housekeeping;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ScrollView;
import com.citymaps.mobile.android.R;
import com.citymaps.mobile.android.app.SessionManager;
import com.citymaps.mobile.android.app.TrackedActionBarActivity;
import com.citymaps.mobile.android.config.Endpoint;
import com.citymaps.mobile.android.config.Environment;
import com.citymaps.mobile.android.model.ThirdPartyUser;
import com.citymaps.mobile.android.model.User;
import com.citymaps.mobile.android.util.IntentUtils;

public class SigninActivity extends TrackedActionBarActivity
		implements SigninFragment.OnSignInListener,
		SigninCreateAccountFragment.OnCreateAccountListener,
		SigninResetPasswordFragment.OnResetPasswordListener {

	public static final String STATE_KEY_SCROLL_POSITION = "scrollPosition";

	public static final int SIGN_IN_MODE = 0;
	public static final int CREATE_ACCOUNT_MODE = 1;
	public static final int RESET_PASSWORD_MODE = 2;

	public static final String URI_PARAM_TERMS_OF_SERVICE = "terms_of_service";
	public static final String URI_PARAM_PRIVACY_POLICY = "privacy_policy";

	private ScrollView mScrollView;

	private ThirdPartyUser mThirdPartyUser;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_signin);

		Intent intent = getIntent();
		mThirdPartyUser = IntentUtils.getThirdPartyUser(intent);

		mScrollView = (ScrollView) findViewById(R.id.login_scrollview);

		final int[] scrollViewPos;
		if (savedInstanceState == null) {
			int loginMode = IntentUtils.getLoginMode(intent, SIGN_IN_MODE);
			showFragment(loginMode, false, false);
			scrollViewPos = new int[]{0, 0};
		} else {
			scrollViewPos = savedInstanceState.getIntArray(STATE_KEY_SCROLL_POSITION);
		}

		// RatioImageView causes unpredictable ScrollView position in landscape orientation
		// so let's set/restore it by posting a Runnable
		new Handler().post(new Runnable() {
			@Override
			public void run() {
				mScrollView.scrollTo(scrollViewPos[0], scrollViewPos[1]);
			}
		});
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putIntArray(STATE_KEY_SCROLL_POSITION,
				new int[]{mScrollView.getScrollX(), mScrollView.getScrollY()});
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);

		/* Handle the "Terms of Service" and "Privacy Policy" links found in LoginCreateAccountFragment.
		These are sent automatically because of the HTML defined in login_create_account_disclaimer in strings.xml
		and re-sent to this Activity because of the intent-filter defined in AndroidManifest.xml */

		// NOTE: I could do this by putting the actual citymaps urls directly in the String but I'm saving it here
		// for now, for future use.

		Environment environment = SessionManager.getInstance(this).getEnvironment();

		Uri data = intent.getData();
		String param = data.getQueryParameter("q");
		if (TextUtils.equals(param, URI_PARAM_TERMS_OF_SERVICE)) {
			Intent newIntent = new Intent(Intent.ACTION_VIEW);
			String urlString = environment.buildUrlString(Endpoint.Type.TERMS_OF_SERVICE);
			newIntent.setData(Uri.parse(urlString));
			startActivity(newIntent);
		} else if (TextUtils.equals(param, URI_PARAM_PRIVACY_POLICY)) {
			Intent newIntent = new Intent(Intent.ACTION_VIEW);
			String urlString = environment.buildUrlString(Endpoint.Type.PRIVACY_POLICY);
			newIntent.setData(Uri.parse(urlString));
			startActivity(newIntent);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onSignInSuccess(User currentUser) {
		finishOk();
	}

	@Override
	public void onSignInCreateAccount() {
		showFragment(CREATE_ACCOUNT_MODE, true, true);
	}

	@Override
	public void onSignInResetPassword() {
		showFragment(RESET_PASSWORD_MODE, true, true);
	}

	@Override
	public void onCreateAccountSuccess(User currentUser) {
		finishOk();
	}

	@Override
	public void onResetPasswordSuccess() {

	}

	@Override
	public void finish() {
		Intent data = new Intent();
		IntentUtils.putThirdPartyUser(data, mThirdPartyUser);
		setResult(RESULT_CANCELED, data);
		super.finish();
	}

	private void finishOk() {
		setResult(RESULT_OK);
		super.finish();
	}

	private void showFragment(int loginMode, boolean animate, boolean addToBackStack) {
		Fragment fragment;
		switch (loginMode) {
			case CREATE_ACCOUNT_MODE:
				if (mThirdPartyUser == null) {
					fragment = SigninCreateAccountFragment.newInstance();
				} else {
					fragment = SigninCreateAccountFragment.newInstance(mThirdPartyUser);
				}
				break;
			case RESET_PASSWORD_MODE:
				fragment = SigninResetPasswordFragment.newInstance("", "");
				break;
			case SIGN_IN_MODE:
			default:
				fragment = SigninFragment.newInstance("", "");
		}

		FragmentManager manager = getSupportFragmentManager();
		FragmentTransaction transaction = manager.beginTransaction();
		if (animate) {
			transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left,
					android.R.anim.slide_in_left, android.R.anim.slide_out_right);
		}
		transaction.replace(R.id.login_fragment_container, fragment);
		if (addToBackStack) {
			transaction.addToBackStack(null);
		}
		transaction.commit();
	}
}
