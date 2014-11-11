package com.citymaps.mobile.android.view.housekeeping;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import com.citymaps.mobile.android.R;

public class EnableLocationDialogFragment extends DialogFragment
		implements DialogInterface.OnClickListener {

	public static final String FRAGMENT_TAG = "enableLocation";

	/**
	 * Use this factory method to create a new instance of
	 * this fragment using the provided parameters.
	 *
	 * @return A new instance of fragment EnableLocationDialogFragment.
	 */
	public static EnableLocationDialogFragment newInstance() {
		return new EnableLocationDialogFragment();
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		switch (which) {
			case DialogInterface.BUTTON_POSITIVE:
				startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
		}
	}

	@NonNull
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		return new AlertDialog.Builder(getActivity())
				.setTitle(R.string.loc_dialog_title)
				.setMessage(R.string.loc_dialog_message)
				.setPositiveButton(R.string.loc_dialog_settings_button_text, this)
				.setNegativeButton(R.string.loc_dialog_skip_button_text, null)
				.create();
	}
}
