package com.citymaps.mobile.android.view.update;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.widget.Toast;
import com.citymaps.mobile.android.R;
import com.citymaps.mobile.android.util.SharedPreferenceUtils;
import com.citymaps.mobile.android.util.UpdateUtils;

/**
 * A simple {@link DialogFragment} subclass for displaying soft update options.
 * Use the {@link SoftUpdateDialogFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SoftUpdateDialogFragment extends DialogFragment {

	public static final String FRAGMENT_TAG = "softUpgrade";

	/**
	 * Use this factory method to create a new instance of
	 * this fragment using the provided parameters.
	 *
	 * @return A new instance of fragment SoftUpgradeDialogFragment.
	 */
	public static SoftUpdateDialogFragment newInstance() {
		return new SoftUpdateDialogFragment();
	}

	private OnClickListener mOnClickListener = new OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			SharedPreferences sp = SharedPreferenceUtils.getConfigSharedPreferences(getActivity());
			sp.edit().putInt(SharedPreferenceUtils.Key.CONFIG_PROCESSED_ACTION.toString(), which)
					.putLong(SharedPreferenceUtils.Key.CONFIG_PROCESSED_TIMESTAMP.toString(), System.currentTimeMillis())
					.apply();
			switch (which) {
				case DialogInterface.BUTTON_POSITIVE: // Update
					UpdateUtils.goToPlayStore(getActivity());
					break;
				case DialogInterface.BUTTON_NEUTRAL: // Later
					Toast.makeText(getActivity(), R.string.update_soft_dialog_reminder, Toast.LENGTH_LONG).show();
					break;
			}
		}
	};

	@NonNull
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		return new AlertDialog.Builder(getActivity())
				.setTitle(R.string.update_soft_dialog_title)
				.setMessage(R.string.update_soft_dialog_message)
				.setPositiveButton(R.string.update_soft_dialog_button_update, mOnClickListener)
				.setNegativeButton(R.string.update_soft_dialog_button_skip, mOnClickListener)
				.setNeutralButton(R.string.update_soft_dialog_button_later, mOnClickListener)
				.create();
	}
}
