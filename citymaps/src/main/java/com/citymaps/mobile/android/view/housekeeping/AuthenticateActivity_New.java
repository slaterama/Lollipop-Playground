package com.citymaps.mobile.android.view.housekeeping;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Toast;
import com.citymaps.mobile.android.R;
import com.citymaps.mobile.android.app.TrackedActionBarActivity;
import com.citymaps.mobile.android.thirdpartynew.ThirdParty;
import com.citymaps.mobile.android.thirdpartynew.ThirdPartyManager;
import com.citymaps.mobile.android.util.IntentUtils;
import com.citymaps.mobile.android.util.LogEx;
import com.citymaps.mobile.android.view.MainActivity;

public class AuthenticateActivity_New extends TrackedActionBarActivity
		implements ThirdParty.ConnectionCallbacks {

	private static final int REQUEST_CODE_LOGIN = 1001;
	private static final int REQUEST_CODE_CREATE_ACCOUNT = 1002;

	private boolean mStartupMode;

	private ConnectivityManager mConnectivityManager;

	private ThirdPartyManager mThirdPartyManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_authenticate);
		mStartupMode = IntentUtils.isStartupMode(getIntent(), false);

		mConnectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
		mThirdPartyManager = new ThirdPartyManager(this, this);
		mThirdPartyManager.add(ThirdParty.Type.FACEBOOK);
		mThirdPartyManager.add(ThirdParty.Type.GOOGLE);
		mThirdPartyManager.onCreate(savedInstanceState);
	}

	@Override
	public void onResume() {
		super.onResume();
		mThirdPartyManager.onResume();
	}

	@Override
	public void onStart() {
		super.onStart();
		mThirdPartyManager.onStart();
	}

	@Override
	public void onSaveInstanceState(@NonNull Bundle outState) {
		super.onSaveInstanceState(outState);
		mThirdPartyManager.onSaveInstanceState(outState);
	}

	@Override
	public void onPause() {
		super.onPause();
		mThirdPartyManager.onPause();
	}

	@Override
	public void onStop() {
		super.onStop();
		mThirdPartyManager.onStop();

		// TODO Begin MY Google
		if (isChangingConfigurations()) {

		}
		// End MY Google
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mThirdPartyManager.onDestroy();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
			case REQUEST_CODE_CREATE_ACCOUNT:
				if (resultCode != RESULT_OK) {
					// TODO What to do here? I think ThirdParty needs a "cancel" method?
				}
			case REQUEST_CODE_LOGIN:
				if (resultCode == RESULT_OK) {
					wrapUp();
				}
				break;
			default:
				super.onActivityResult(requestCode, resultCode, data);
				mThirdPartyManager.onActivityResult(requestCode, resultCode, data);
		}
	}

	public void onButtonClick(View view) {
		int id = view.getId();
		switch (id) {
			case R.id.login_authenticate_facebook_button:
			case R.id.login_authenticate_google_button: {
				if (mConnectivityManager.getActiveNetworkInfo() == null) {
					Toast.makeText(this, R.string.error_message_no_connection, Toast.LENGTH_SHORT).show();
					return;
				}
				ThirdParty thirdParty = null;
				switch (id) {
					case R.id.login_authenticate_facebook_button:
						thirdParty = mThirdPartyManager.get(ThirdParty.Type.FACEBOOK);
						break;
					case R.id.login_authenticate_google_button:
						thirdParty = mThirdPartyManager.get(ThirdParty.Type.GOOGLE);
						break;
				}

				if (thirdParty != null) {

					// TODO Causing problems with concurrency.

					thirdParty.getToken(ThirdParty.Mode.INTERACTIVE, new ThirdParty.TokenCallbacks() {
						@Override
						public void onSuccess(String data) {
							if (LogEx.isLoggable(LogEx.INFO)) {
								LogEx.i(String.format("token=%s", data));
							}
						}

						@Override
						public void onError(Exception error) {
							if (LogEx.isLoggable(LogEx.INFO)) {
								LogEx.i();
							}
						}
					});

					thirdParty.getUser(ThirdParty.Mode.INTERACTIVE, new ThirdParty.UserCallbacks() {
						@Override
						public void onSuccess(ThirdParty.UserProxy user) {
							if (LogEx.isLoggable(LogEx.INFO)) {
								LogEx.i(String.format("name=%s %s", user.getFirstName(), user.getLastName()));
							}
						}

						@Override
						public void onError(Object error) {
							if (LogEx.isLoggable(LogEx.INFO)) {
								LogEx.i();
							}
						}
					});
				}
				break;
			}
			case R.id.login_authenticate_create_account_button: {
				Intent intent = new Intent(this, LoginActivity.class);
				IntentUtils.putLoginMode(intent, LoginActivity.CREATE_ACCOUNT_MODE);
				startActivityForResult(intent, REQUEST_CODE_CREATE_ACCOUNT);
				break;
			}
			case R.id.login_authenticate_signin_button: {
				Intent intent = new Intent(this, LoginActivity.class);
				IntentUtils.putLoginMode(intent, LoginActivity.SIGN_IN_MODE);
				startActivityForResult(intent, REQUEST_CODE_LOGIN);
				break;
			}
			case R.id.login_authenticate_skip_button: {
				wrapUp();
				break;
			}
		}
	}

	private void wrapUp() {
		if (mStartupMode) {
			startActivity(new Intent(this, MainActivity.class));
		}
		finish();
	}

	@Override
	public void onConnected(ThirdParty thirdParty) {
		/*
		if (LogEx.isLoggable(LogEx.INFO)) {
			LogEx.i(String.format("thirdParty=%s", thirdParty));
		}
		*/
	}

	@Override
	public void onDisconnected(ThirdParty thirdParty) {
		/*
		if (LogEx.isLoggable(LogEx.INFO)) {
			LogEx.i(String.format("thirdParty=%s", thirdParty));
		}
		*/
	}

	@Override
	public void onError(ThirdParty thirdParty) {
		/*
		if (LogEx.isLoggable(LogEx.INFO)) {
			LogEx.i(String.format("thirdParty=%s", thirdParty));
		}
		*/
	}
}