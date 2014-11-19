package com.citymaps.mobile.android.preference;

import android.content.Context;
import android.preference.ListPreference;
import android.preference.Preference;
import android.text.TextUtils;
import android.util.AttributeSet;

/**
 * <p>A {@link android.preference.ListPreference} that invokes a callback when the user
 * re-selects the value in the ListPreference that was already selected.</p>
 */
public class ListPreferenceEx extends ListPreference {

	/**
	 * The value when the ListPreference was clicked.
	 */
	protected String mOnClickValue;

	/**
	 * The callback to be invoked when the user re-selects the current value.
	 */
	protected OnPreferenceReselectedListener mOnPreferenceReselectedListener;

	public ListPreferenceEx(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ListPreferenceEx(Context context) {
		super(context);
	}

	/**
	 * If the dialog was closed with a positive result, compares the new value to the previous
	 * value and invokes the OnPreferenceReselectedListener callback if the value was re-selected.
	 */
	@Override
	protected void onDialogClosed(boolean positiveResult) {
		super.onDialogClosed(positiveResult);
		if (positiveResult) {
			String newValue = getValue();
			if (TextUtils.equals(mOnClickValue, newValue)) {
				if (mOnPreferenceReselectedListener != null)
					mOnPreferenceReselectedListener.onPreferenceReselected(this, newValue);
			}
			mOnClickValue = null;
		}

	}

	/**
	 * Saves the value of this ListPreference at the time it was clicked.
	 */
	@Override
	protected void onClick() {
		super.onClick();
		mOnClickValue = getValue();
	}

	/**
	 * Sets the callback to be invoked when the user re-selects the current value.
	 * @param onPreferenceReselectedListener The callback to be invoked.
	 */
	public void setOnPreferenceReselectedListener(OnPreferenceReselectedListener onPreferenceReselectedListener) {
		mOnPreferenceReselectedListener = onPreferenceReselectedListener;
	}

	/**
	 * Returns the callback to be invoked when the user re-selects the current value.
	 * @return The callback to be invoked.
	 */
	public OnPreferenceReselectedListener getOnPreferenceReselectedListener() {
		return mOnPreferenceReselectedListener;
	}

	/**
	 * Interface definition for a callback to be invoked when the user re-selects the current value
	 * of this Preference.
	 */
	public static interface OnPreferenceReselectedListener {
		public void onPreferenceReselected(Preference preference, Object value);
	}
}
