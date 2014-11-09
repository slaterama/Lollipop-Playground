package com.citymaps.mobile.android.util.softupdatecompat;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import com.citymaps.mobile.android.model.vo.Config;

public class SoftUpdateCompatFragmentActivity extends SoftUpdateCompat {

	private FragmentActivity mActivity;

	public SoftUpdateCompatFragmentActivity(FragmentActivity activity) {
		super();
		mActivity = activity;
	}

	@Override
	public void showSoftUpdateDialogFragment(Config config) {
		FragmentManager fragmentManager = mActivity.getSupportFragmentManager();
		DialogFragment fragment = (DialogFragment) fragmentManager.findFragmentByTag(DIALOG_FRAGMENT_TAG);
		if (fragment == null) {
			fragment = new SoftUpdateDialogFragment();
			Bundle args = new Bundle();
			args.putParcelable(ARG_CONFIG, config);
			fragment.setArguments(args);
			fragment.show(fragmentManager, DIALOG_FRAGMENT_TAG);
		}
	}

	public static class SoftUpdateDialogFragment extends DialogFragment {
		@NonNull
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			Config config = getArguments().getParcelable(ARG_CONFIG);
			return SoftUpdateCompat.buildDialog(getActivity(), config);
		}
	}
}
