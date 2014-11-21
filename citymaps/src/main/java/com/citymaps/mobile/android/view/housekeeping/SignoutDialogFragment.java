package com.citymaps.mobile.android.view.housekeeping;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import com.citymaps.mobile.android.R;

@SuppressWarnings("SpellCheckingInspection")
public class SignoutDialogFragment extends DialogFragment {

	public static final String FRAGMENT_TAG = SignoutDialogFragment.class.getName();

	public static SignoutDialogFragment newInstance() {
		return new SignoutDialogFragment();
	}

	private OnSignoutListener mListener;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mListener = (OnSignoutListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnSignoutListener");
		}
	}

	@NonNull
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		return new AlertDialog.Builder(getActivity())
				.setTitle(R.string.pref_signout_dialog_title)
				.setMessage(R.string.pref_signout_dialog_message)
				.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						mListener.onSignout();
					}
				})
				.setNegativeButton(android.R.string.cancel, null)
				.show();
	}

	public static interface OnSignoutListener {
		public void onSignout();
	}
}
