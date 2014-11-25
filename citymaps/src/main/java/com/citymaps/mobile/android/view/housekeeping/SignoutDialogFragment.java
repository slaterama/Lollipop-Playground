package com.citymaps.mobile.android.view.housekeeping;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import com.citymaps.mobile.android.R;

@SuppressWarnings("SpellCheckingInspection")
public class SignoutDialogFragment extends DialogFragment
		implements DialogInterface.OnClickListener {

	public static final String FRAGMENT_TAG = SignoutDialogFragment.class.getName();

	public static SignoutDialogFragment newInstance(OnSignoutListener listener) {
		SignoutDialogFragment fragment = new SignoutDialogFragment();
		fragment.setListener(listener);
		return fragment;
	}

	public static SignoutDialogFragment newInstance() {
		return newInstance(null);
	}

	private OnSignoutListener mListener;

	public void setListener(OnSignoutListener listener) {
		mListener = listener;
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		switch (which) {
			case DialogInterface.BUTTON_POSITIVE:
				if (mListener != null) {
					mListener.onSignout();
				} else if (getTargetFragment() != null) {
					getTargetFragment().onActivityResult(getTargetRequestCode(), FragmentActivity.RESULT_OK, null);
				}
				break;
		}
	}

	@NonNull
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		return new AlertDialog.Builder(getActivity())
				.setTitle(R.string.pref_signout_dialog_title)
				.setMessage(R.string.pref_signout_dialog_message)
				.setPositiveButton(android.R.string.ok, SignoutDialogFragment.this)
				.setNegativeButton(android.R.string.cancel, null)
				.show();
	}

	public static interface OnSignoutListener {
		public void onSignout();
	}
}
