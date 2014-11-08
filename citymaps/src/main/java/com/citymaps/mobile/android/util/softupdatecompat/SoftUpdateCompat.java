package com.citymaps.mobile.android.util.softupdatecompat;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.app.FragmentActivity;
import com.citymaps.mobile.android.R;
import com.citymaps.mobile.android.util.LogEx;

public abstract class SoftUpdateCompat {

	protected final static String DIALOG_FRAGMENT_TAG = "softUpdateDialogFragment";

	protected static Dialog buildDialog(Context context) {
		return new AlertDialog.Builder(context)
				.setTitle(R.string.update_soft_dialog_title)
				.setMessage(R.string.update_soft_dialog_message)
				.setNegativeButton(R.string.update_soft_dialog_negative_button, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i) {
						LogEx.d();
					}
				})
				.setPositiveButton(R.string.update_soft_dialog_positive_button, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i) {
						LogEx.d();
					}
				})
				.create();
	}

	public static SoftUpdateCompat newInstance(Activity activity) {
		if (activity instanceof FragmentActivity) {
			return new SoftUpdateCompatFragmentActivity((FragmentActivity) activity);
		} else {
			return new SoftUpdateCompatActivity(activity);
		}
	}

	protected SoftUpdateCompat() {
	}

	public abstract void showSoftUpdateDialogFragment();
}
