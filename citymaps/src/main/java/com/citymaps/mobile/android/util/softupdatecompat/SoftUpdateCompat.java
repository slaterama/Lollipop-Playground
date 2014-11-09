package com.citymaps.mobile.android.util.softupdatecompat;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.app.FragmentActivity;
import com.citymaps.mobile.android.R;
import com.citymaps.mobile.android.model.vo.Config;
import com.citymaps.mobile.android.util.LogEx;
import com.citymaps.mobile.android.util.SharedPreferenceUtils;

public abstract class SoftUpdateCompat {

	protected final static String DIALOG_FRAGMENT_TAG = "softUpdateDialogFragment";

	protected final static String ARG_CONFIG = "config";

	protected static Dialog buildDialog(final Context context, final Config config) {
		return new AlertDialog.Builder(context)
				.setTitle(R.string.update_soft_dialog_title)
				.setMessage(R.string.update_soft_dialog_message)
				.setNegativeButton(R.string.update_soft_dialog_negative_button, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i) {
						int appVersionCode = config.getAppVersionCode();
						SharedPreferenceUtils.applyLastDismissedVersionCode(context, appVersionCode);
						LogEx.d(String.format("%s: %d",
								context.getString(R.string.update_soft_dialog_negative_button), appVersionCode));
					}
				})
				.setPositiveButton(R.string.update_soft_dialog_positive_button, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i) {
						int appVersionCode = config.getAppVersionCode();
						SharedPreferenceUtils.applyLastDismissedVersionCode(context, appVersionCode);
						LogEx.d(String.format("%s: %d",
								context.getString(R.string.update_soft_dialog_positive_button), appVersionCode));
					}
				})
				.create();
	}

	public static SoftUpdateCompat newInstance(Activity activity, Config config) {
		if (activity instanceof FragmentActivity) {
			return new SoftUpdateCompatFragmentActivity((FragmentActivity) activity, config);
		} else {
			return new SoftUpdateCompatActivity(activity, config);
		}
	}

	protected Config mConfig;

	protected SoftUpdateCompat(Config config) {
		mConfig = config;
	}

	public abstract void showSoftUpdateDialogFragment();
}
