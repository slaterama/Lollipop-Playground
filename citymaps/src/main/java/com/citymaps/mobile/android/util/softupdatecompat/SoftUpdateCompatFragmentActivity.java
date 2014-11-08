package com.citymaps.mobile.android.util.softupdatecompat;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

public class SoftUpdateCompatFragmentActivity extends SoftUpdateCompat {

	private FragmentActivity mActivity;

	public SoftUpdateCompatFragmentActivity(FragmentActivity activity) {
		super();
		mActivity = activity;
	}

	@Override
	public void showSoftUpdateDialogFragment() {
		FragmentManager fragmentManager = mActivity.getSupportFragmentManager();
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
