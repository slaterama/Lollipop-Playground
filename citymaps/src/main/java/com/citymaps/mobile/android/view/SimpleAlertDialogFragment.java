package com.citymaps.mobile.android.view;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

/**
 * A simple {@link DialogFragment} subclass.
 * Use the {@link SimpleAlertDialogFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SimpleAlertDialogFragment extends DialogFragment {

	private static final String ARG_ICON_RES_ID = "iconResId";
	private static final String ARG_TITLE_RES_ID = "titleResId";
	private static final String ARG_TITLE = "title";
	private static final String ARG_MESSAGE_RES_ID = "messageResId";
	private static final String ARG_MESSAGE = "message";

	private static SimpleAlertDialogFragment newInstance(Integer iconResId, Integer titleResId, CharSequence title, Integer messageResId, CharSequence message) {
		SimpleAlertDialogFragment fragment = new SimpleAlertDialogFragment();
		Bundle args = new Bundle();
		if (iconResId != null) {
			args.putInt(ARG_ICON_RES_ID, iconResId);
		}
		if (titleResId != null) {
			args.putInt(ARG_TITLE_RES_ID, titleResId);
		}
		if (title != null) {
			args.putCharSequence(ARG_TITLE, title);
		}
		if (messageResId != null) {
			args.putInt(ARG_MESSAGE_RES_ID, messageResId);
		}
		if (message != null) {
			args.putCharSequence(ARG_MESSAGE, message);
		}
		fragment.setArguments(args);
		return fragment;
	}

	public static SimpleAlertDialogFragment newInstance(int iconResId, int titleResId, int messageResId) {
		return newInstance(iconResId, titleResId, null, messageResId, null);
	}

	public static SimpleAlertDialogFragment newInstance(int iconResId, int titleResId, CharSequence message) {
		return newInstance(iconResId, titleResId, null, null, message);
	}

	public static SimpleAlertDialogFragment newInstance(int iconResId, CharSequence title, int messageResId) {
		return newInstance(iconResId, null, title, messageResId, null);
	}

	public static SimpleAlertDialogFragment newInstance(int iconResId, CharSequence title, CharSequence message) {
		return newInstance(iconResId, null, title, null, message);
	}

	public static SimpleAlertDialogFragment newInstance(int titleResId, int messageResId) {
		return newInstance(null, titleResId, null, messageResId, null);
	}

	public static SimpleAlertDialogFragment newInstance(int titleResId, CharSequence message) {
		return newInstance(null, titleResId, null, null, message);
	}

	public static SimpleAlertDialogFragment newInstance(CharSequence title, int messageResId) {
		return newInstance(null, null, title, messageResId, null);
	}

	public static SimpleAlertDialogFragment newInstance(CharSequence title, CharSequence message) {
		return newInstance(null, null, title, null, message);
	}

	public SimpleAlertDialogFragment() {
		// Required empty public constructor
	}

	@NonNull
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Bundle args = getArguments();
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		if (args.containsKey(ARG_ICON_RES_ID)) {
			builder.setIcon(args.getInt(ARG_ICON_RES_ID));
		}
		if (args.containsKey(ARG_TITLE_RES_ID)) {
			builder.setTitle(args.getInt(ARG_TITLE_RES_ID));
		} else if (args.containsKey(ARG_TITLE)) {
			builder.setTitle(args.getCharSequence(ARG_TITLE));
		}
		if (args.containsKey(ARG_MESSAGE_RES_ID)) {
			builder.setMessage(args.getInt(ARG_MESSAGE_RES_ID));
		} else if (args.containsKey(ARG_MESSAGE)) {
			builder.setMessage(args.getCharSequence(ARG_MESSAGE));
		}
		builder.setPositiveButton(android.R.string.ok,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						dismiss();
					}
				}
		);
		return builder.create();
	}
}
