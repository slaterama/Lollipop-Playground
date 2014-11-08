package com.citymaps.mobile.android.util.softupdatecompat;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.annotation.NonNull;

public class SoftUpdateCompatActivity extends SoftUpdateCompat {

	private Activity mActivity;

	public SoftUpdateCompatActivity(Activity activity) {
		super();
		mActivity = activity;
	}

	@Override
	public void showSoftUpdateDialogFragment() {
		FragmentManager fragmentManager = mActivity.getFragmentManager();
		DialogFragment fragment = (DialogFragment) fragmentManager.findFragmentByTag(DIALOG_FRAGMENT_TAG);
		if (fragment == null) {
			fragment = new SoftUpdateDialogFragment();
			fragment.show(fragmentManager, DIALOG_FRAGMENT_TAG);
		}
	}

	public static class SoftUpdateDialogFragment extends DialogFragment {
		@NonNull
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			return SoftUpdateCompat.buildDialog(getActivity());
		}
	}
}
