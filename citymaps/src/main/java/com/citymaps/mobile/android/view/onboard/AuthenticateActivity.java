package com.citymaps.mobile.android.view.onboard;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.citymaps.mobile.android.R;
import com.citymaps.mobile.android.util.LogEx;

public class AuthenticateActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		if (!getResources().getBoolean(R.bool.authenticate_allow_orientation_change)) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
        setContentView(R.layout.activity_authenticate);
    }

	public void onButtonClick(View view) {
		int id = view.getId();
		switch (id) {
			case R.id.login_authenticate_facebook_button:
				LogEx.d(((Button) view).getText().toString());
				break;
			case R.id.login_authenticate_google_button:
				LogEx.d(((Button) view).getText().toString());
				break;
			case R.id.login_authenticate_create_account_button:
				LogEx.d(((Button) view).getText().toString());
				break;
			case R.id.login_authenticate_login_button:
				LogEx.d(((Button) view).getText().toString());
				break;
			case R.id.login_authenticate_skip_button:
				LogEx.d(((Button) view).getText().toString());
				break;
		}
	}
}
