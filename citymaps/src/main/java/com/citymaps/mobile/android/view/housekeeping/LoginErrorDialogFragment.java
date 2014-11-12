package com.citymaps.mobile.android.view.housekeeping;


import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import com.citymaps.mobile.android.R;

/**
 * A simple {@link DialogFragment} subclass.
 *
 */
public class LoginErrorDialogFragment extends DialogFragment {

	public static final String FRAGMENT_TAG = "loginError";

	private static final String ARG_TITLE = "title";
	private static final String ARG_MESSAGE = "message";

	public static LoginErrorDialogFragment newInstance(CharSequence title, CharSequence message) {
		LoginErrorDialogFragment fragment = new LoginErrorDialogFragment();
		Bundle args = new Bundle(2);
		args.putCharSequence(ARG_TITLE, title);
		args.putCharSequence(ARG_MESSAGE, message);
		fragment.setArguments(args);
		return fragment;
	}

	@NonNull
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Bundle args = getArguments();
		CharSequence title = args.getCharSequence(ARG_TITLE, getString(R.string.app_name));
		CharSequence message = args.getCharSequence(ARG_MESSAGE, getString(R.string.app_error_message_generic));
		return new AlertDialog.Builder(getActivity())
				.setTitle(title)
				.setMessage(message)
				.setPositiveButton(android.R.string.ok, null)
				.create();
	}
}
