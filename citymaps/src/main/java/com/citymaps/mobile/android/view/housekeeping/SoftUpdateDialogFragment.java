package com.citymaps.mobile.android.view.housekeeping;

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
import com.citymaps.mobile.android.util.CitymapsPreference;
import com.citymaps.mobile.android.util.SharedPrefUtils;
import com.citymaps.mobile.android.util.UpdateUtils;

/**
 * A simple {@link DialogFragment} subclass for displaying soft update options.
 * Use the {@link SoftUpdateDialogFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SoftUpdateDialogFragment extends DialogFragment
		implements OnClickListener {

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

	@Override
	public void onClick(DialogInterface dialog, int which) {
		SharedPreferences sp = SharedPrefUtils.getConfigSharedPreferences(getActivity());
		sp.edit().putInt(CitymapsPreference.CONFIG_PROCESSED_ACTION.getKey(), which)
				.putLong(CitymapsPreference.CONFIG_PROCESSED_TIMESTAMP.getKey(), System.currentTimeMillis())
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

	@NonNull
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		return new AlertDialog.Builder(getActivity())
				.setTitle(R.string.update_soft_dialog_title)
				.setMessage(R.string.update_soft_dialog_message)
				.setPositiveButton(R.string.update_soft_dialog_udpate_button_text, this)
				.setNegativeButton(R.string.update_soft_dialog_skip_button_text, this)
				.setNeutralButton(R.string.update_soft_dialog_later_button_text, this)
				.create();
	}
}
