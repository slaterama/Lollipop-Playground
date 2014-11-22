package com.citymaps.mobile.android.util;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.widget.Toast;
import com.citymaps.mobile.android.R;

public class CommonUtils {

	public static boolean notifyIfNoNetwork(Context context) {
		ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (manager.getActiveNetworkInfo() == null) {
			Toast.makeText(context, R.string.error_message_no_connection, Toast.LENGTH_SHORT).show();
			return true;
		}
		return false;
	}

	public static void showSimpleDialogFragment(FragmentManager fragmentManager,
												CharSequence title, CharSequence message, boolean replaceIfFound) {
		SimpleDialogFragment fragment = (SimpleDialogFragment) fragmentManager.findFragmentByTag(SimpleDialogFragment.FRAGMENT_TAG);
		if (fragment != null) {
			if (replaceIfFound) {
				fragment.dismiss();
			} else {
				return;
			}
		}
		fragment = SimpleDialogFragment.newInstance(title, message);
		fragment.show(fragmentManager, SimpleDialogFragment.FRAGMENT_TAG);
	}

	public static void showSimpleDialogFragment(FragmentManager fragmentManager,
												CharSequence title, CharSequence message) {
		showSimpleDialogFragment(fragmentManager, title, message, true);
	}

	private CommonUtils() {
	}

	/**
	 * A simple {@link android.support.v4.app.DialogFragment} subclass.
	 */
	public static class SimpleDialogFragment extends DialogFragment {

		public static final String FRAGMENT_TAG = SimpleDialogFragment.class.getName();

		private static final String ARG_TITLE = "title";
		private static final String ARG_MESSAGE = "message";

		public static SimpleDialogFragment newInstance(CharSequence title, CharSequence message) {
			SimpleDialogFragment fragment = new SimpleDialogFragment();
			Bundle args = new Bundle(2);
			if (!TextUtils.isEmpty(title)) {
				args.putCharSequence(ARG_TITLE, title);
			}
			if (!TextUtils.isEmpty(message)) {
				args.putCharSequence(ARG_MESSAGE, message);
			}
			fragment.setArguments(args);
			return fragment;
		}

		@NonNull
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			Bundle args = getArguments();
			CharSequence title = args.getCharSequence(ARG_TITLE, getString(R.string.app_name));
			CharSequence message = args.getCharSequence(ARG_MESSAGE, getString(R.string.error_message_generic));
			return new AlertDialog.Builder(getActivity())
					.setTitle(title)
					.setMessage(message)
					.setPositiveButton(android.R.string.ok, null)
					.create();
		}
	}
}
