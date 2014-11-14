package com.citymaps.mobile.android.util;

import android.content.Context;
import android.text.TextUtils;
import android.util.Patterns;
import com.citymaps.mobile.android.BuildConfig;
import com.citymaps.mobile.android.R;
import org.apache.commons.lang3.ArrayUtils;

import java.util.regex.Pattern;

public enum Validator {
	EMAIL(Patterns.EMAIL_ADDRESS,
			0, new Object[0],
			R.string.validator_missing_your_email, new Object[0],
			R.string.validator_invalid_email, new Object[0],
			R.string.validator_no_match_email, new Object[0]),

	FIRST_NAME(CitymapsPatterns.NAME,
			0, new Object[0],
			R.string.validator_missing_your_first_name, new Object[0],
			R.string.validator_invalid_first_name, new Object[]{BuildConfig.NAME_MIN_LENGTH, BuildConfig.NAME_MAX_LENGTH},
			0, new Object[0]),

	LAST_NAME(CitymapsPatterns.NAME,
			0, new Object[0],
			R.string.validator_missing_your_last_name, new Object[0],
			R.string.validator_invalid_last_name, new Object[]{BuildConfig.NAME_MIN_LENGTH, BuildConfig.NAME_MAX_LENGTH},
			0, new Object[0]),

	PASSWORD(CitymapsPatterns.PASSWORD,
			R.string.validator_missing_a_password, new Object[0],
			R.string.validator_missing_your_password, new Object[0],
			R.string.validator_invalid_password, new Object[]{BuildConfig.PASSWORD_MIN_LENGTH, BuildConfig.PASSWORD_MAX_LENGTH},
			R.string.validator_no_match_password, new Object[0]),

	USERNAME(CitymapsPatterns.USERNAME,
			R.string.validator_missing_a_username, new Object[0],
			R.string.validator_missing_your_username, new Object[0],
			R.string.validator_invalid_username, new Object[]{BuildConfig.USERNAME_MIN_LENGTH, BuildConfig.USERNAME_MAX_LENGTH},
			0, new Object[0]);

	private Pattern mPattern;
	private int mMissingAValueResId;
	private Object[] mMissingAValueArgs;
	private int mMissingYourValueResId;
	private Object[] mMissingYourValueArgs;
	private int mInvalidValueResId;
	private Object[] mInvalidValueArgs;
	private int mNoMatchResId;
	private Object[] mNoMatchArgs;

	private Validator(Pattern pattern,
					  int missingAValueResId, Object[] missingAValueArgs,
					  int missingYourValueResId, Object[] missingYourValueArgs,
					  int invalidValueResId, Object[] invalidValueArgs,
					  int noMatchResId, Object[] noMatchArgs) {
		mPattern = pattern;
		mMissingAValueResId = missingAValueResId;
		mMissingAValueArgs = missingAValueArgs;
		mMissingYourValueResId = missingYourValueResId;
		mMissingYourValueArgs = missingYourValueArgs;
		mInvalidValueResId = invalidValueResId;
		mInvalidValueArgs = invalidValueArgs;
		mNoMatchResId = noMatchResId;
		mNoMatchArgs = noMatchArgs;
	}

	public boolean validate(CharSequence input, boolean required) {
		if (required) {
			if (TextUtils.isEmpty(input)) {
				return false;
			}
		}

		if (mPattern != null) {
			if (input == null || !mPattern.matcher(input).matches()) {
				return false;
			}
		}

		return true;
	}

	public boolean equals(CharSequence input1, CharSequence input2) {
		return TextUtils.equals(input1, input2);
	}

	public String getMessage(Context context, CharSequence input, boolean required, boolean your, Object... args) {
		if (required) {
			if (TextUtils.isEmpty(input)) {
				boolean[] tryOrder = new boolean[]{your, !your};
				for (boolean value : tryOrder) {
					if (value) {
						if (mMissingYourValueResId != 0) {
							Object[] combinedArgs = ArrayUtils.addAll(mMissingYourValueArgs, args);
							return context.getString(mMissingYourValueResId, combinedArgs);
						}
					} else if (mMissingAValueResId != 0) {
						Object[] combinedArgs = ArrayUtils.addAll(mMissingAValueArgs, args);
						return context.getString(mMissingAValueResId, combinedArgs);
					}
				}

				return context.getString(R.string.validator_missing_generic);
			}
		}

		if (mPattern != null) {
			if (input == null || !mPattern.matcher(input).matches()) {
				if (mInvalidValueResId != 0) {
					Object[] combinedArgs = ArrayUtils.addAll(mInvalidValueArgs, args);
					return context.getString(mInvalidValueResId, combinedArgs);
				}

				return context.getString(R.string.validator_invalid_generic, input);
			}
		}

		return "";
	}

	public String getMessage(Context context, CharSequence input1, CharSequence input2, Object... args) {
		if (!TextUtils.equals(input1, input2)) {
			if (mNoMatchResId != 0) {
				Object[] combinedArgs = ArrayUtils.addAll(mNoMatchArgs, args);
				return context.getString(mNoMatchResId, combinedArgs);
			}

			return context.getString(R.string.validator_no_match_generic);
		}

		return "";
	}
}
