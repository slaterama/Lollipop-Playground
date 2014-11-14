package com.citymaps.mobile.android.view.housekeeping;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Patterns;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import com.citymaps.mobile.android.BuildConfig;
import com.citymaps.mobile.android.R;
import com.citymaps.mobile.android.util.CitymapsPatterns;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

public abstract class LoginFragment extends Fragment
		implements TextView.OnEditorActionListener {

	protected static int VALIDATION_KEY_USERNAME = 0;
	protected static int VALIDATION_KEY_PASSOWRD = 1;
	protected static int VALIDATION_KEY_FIRST_NAME = 2;
	protected static int VALIDATION_KEY_LAST_NAME = 3;
	protected static int VALIDATION_KEY_EMAIL = 4;

	protected static SparseArray<ValidationHelper> sValidationHelperArray;

	protected static ValidationHelper getHelper(Context context, int key) {
		if (sValidationHelperArray == null) {
			sValidationHelperArray = new SparseArray<ValidationHelper>();
			sValidationHelperArray.put(VALIDATION_KEY_EMAIL,
					new ValidationHelper(Patterns.EMAIL_ADDRESS,
							context.getString(R.string.error_login_missing_email_message),
							context.getString(R.string.error_login_invalid_email_message)));
			sValidationHelperArray.put(VALIDATION_KEY_FIRST_NAME,
					new ValidationHelper(CitymapsPatterns.NAME,
							context.getString(R.string.error_login_missing_first_name_message),
							context.getString(R.string.error_login_invalid_first_name_message,
									BuildConfig.NAME_MIN_LENGTH, BuildConfig.NAME_MAX_LENGTH)));
			sValidationHelperArray.put(VALIDATION_KEY_LAST_NAME,
					new ValidationHelper(CitymapsPatterns.NAME,
							context.getString(R.string.error_login_missing_last_name_message),
							context.getString(R.string.error_login_invalid_last_name_message,
									BuildConfig.NAME_MIN_LENGTH, BuildConfig.NAME_MAX_LENGTH)));
			sValidationHelperArray.put(VALIDATION_KEY_PASSOWRD,
					new ValidationHelper(CitymapsPatterns.PASSWORD,
							context.getString(R.string.error_login_missing_password_message),
							context.getString(R.string.error_login_invalid_password_message,
									BuildConfig.PASSWORD_MIN_LENGTH, BuildConfig.PASSWORD_MAX_LENGTH)));
			sValidationHelperArray.put(VALIDATION_KEY_USERNAME,
					new ValidationHelper(CitymapsPatterns.USERNAME,
							context.getString(R.string.error_login_missing_username_message),
							context.getString(R.string.error_login_invalid_username_message,
									BuildConfig.USERNAME_MIN_LENGTH, BuildConfig.USERNAME_MAX_LENGTH)));
		}
		return sValidationHelperArray.get(key);
	}

	protected static class ValidationHelper {
		private Pattern mPattern;
		private CharSequence mMissingValueMessage;
		private CharSequence mInvalidValueMessage;

		public ValidationHelper(Pattern pattern, CharSequence missingValueMessage,
								CharSequence invalidValueMessage) {
			mPattern = pattern;
			mMissingValueMessage = missingValueMessage;
			mInvalidValueMessage = invalidValueMessage;
		}
	}

	protected Map<EditText, Integer> mValidationMap;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		mValidationMap = new LinkedHashMap<EditText, Integer>();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		switch (id) {
			case R.id.action_submit:
				processInput();
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		if (actionId == EditorInfo.IME_ACTION_GO) {
			processInput();
			return true;
		}
		return false;
	}

	protected void processInput() {
		if (!validateFields()) {
			return;
		}

		onSubmit();
	}

	protected boolean validateFields() {
		Set<EditText> keySet = mValidationMap.keySet();
		for (EditText editText : keySet) {
			// Do something
			Editable text = editText.getText();
			boolean required = true; // TODO
			if (required && TextUtils.isEmpty(text)) {
				// TODO Show msg
				return false;
			}

			int key = mValidationMap.get(editText);
			ValidationHelper helper = getHelper(getActivity(), key);
			if (!helper.mPattern.matcher(text).matches()) {
				// TODO Show msg
				return false;
			}
		}

		return true;
	}

	protected abstract void onSubmit();
}
