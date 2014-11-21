package com.citymaps.mobile.android.view.settings;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.LabeledIntent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.Preference;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import com.citymaps.mobile.android.BuildConfig;
import com.citymaps.mobile.android.R;
import com.citymaps.mobile.android.app.SessionManager;
import com.citymaps.mobile.android.model.User;
import com.citymaps.mobile.android.util.LogEx;

import java.util.ArrayList;
import java.util.List;

public class MainPreferencesFragment extends PreferencesFragment
		implements Preference.OnPreferenceClickListener,
		Preference.OnPreferenceChangeListener {

	private static final int REQUEST_CODE_SHARE_APP = 0;

	private static final String TEXT_INTENT_TYPE = "text/plain";
	private static final String HTML_INTENT_TYPE = "text/html";
	private static final String EMAIL_INTENT_TYPE = "message/rfc822";

	private Preference mShareApp;
	private Preference mPreferenceSendFeedback;

	public static MainPreferencesFragment newInstance() {
		return new MainPreferencesFragment();
	}

	protected User mCurrentUser;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mCurrentUser = SessionManager.getInstance(activity).getCurrentUser();
	}

	@Override
	public void onCreate(Bundle paramBundle) {
		super.onCreate(paramBundle);
		Fragment fragment = getFragmentManager().findFragmentByTag(ShareAppDialogFragment.FRAGMENT_TAG);
		if (fragment != null) {
			fragment.setTargetFragment(this, REQUEST_CODE_SHARE_APP);
		}
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		addPreferencesFromResource(R.xml.preferences_general);

		addPreferencesFromResource(mCurrentUser == null
				? R.xml.preferences_unauthenticated
				: R.xml.preferences_authenticated);

		mShareApp = findPreference(PreferenceType.SHARE_APP.toString());
		mPreferenceSendFeedback = findPreference(PreferenceType.FEEDBACK.toString());

		mShareApp.setOnPreferenceClickListener(this);
		//mShareApp.setOnPreferenceChangeListener(this);
		mPreferenceSendFeedback.setOnPreferenceClickListener(this);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
			case REQUEST_CODE_SHARE_APP:
				LogEx.d(String.format("requestCode=%d, resultCode=%d", requestCode, resultCode));
				break;
			default:
				super.onActivityResult(requestCode, resultCode, data);
		}
	}

	@Override
	public boolean onPreferenceClick(Preference preference) {
		PreferenceType type = PreferenceType.fromKey(preference.getKey());
		switch (type) {
			case SHARE_APP: {

				Intent textIntent = new Intent(Intent.ACTION_SEND);
				textIntent.setType(TEXT_INTENT_TYPE);
				textIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.pref_share_app_email_subject));
				textIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.pref_share_app_email_text));

				PackageManager packageManager = getActivity().getPackageManager();
				List<ResolveInfo> infos = packageManager.queryIntentActivities(textIntent, 0);
				for (ResolveInfo info : infos) {
					String label = String.valueOf(packageManager.getApplicationLabel(info.activityInfo.applicationInfo));
					LogEx.d(String.format("label=%s, priority=%d, isDefault=%b, resolveInfo=%s", label, info.priority, info.isDefault, info));
				}

				Intent chooserIntent = Intent.createChooser(textIntent, getString(R.string.pref_share_app_title));
				startActivity(chooserIntent);

				// TODO Use "specific intents" in queryIntentActivityOptions ?

				/*
				// From the answer section of http://stackoverflow.com/questions/9730243/android-how-to-filter-specific-apps-for-action-send-intent

				// Start with email intent
				Intent emailIntent = new Intent();
				emailIntent.setAction(Intent.ACTION_SEND);
				emailIntent.setType(TEXT_INTENT_TYPE);
				emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{});
				emailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.pref_share_app_email_subject));
				emailIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.pref_share_app_email_text));

				// Create a chooser based on email intent
				Intent openInChooser = Intent.createChooser(emailIntent, getString(R.string.pref_share_chooser_text));
				List<LabeledIntent> intentList = new ArrayList<LabeledIntent>();

				// Add "plain" intents to it
				Intent textIntent = new Intent();
				textIntent.setAction(Intent.ACTION_SEND);
				textIntent.setType(TEXT_INTENT_TYPE);
				textIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.pref_share_app_text));

				List<ResolveInfo> infos = getActivity().getPackageManager().queryIntentActivities(textIntent, 0);
				for (ResolveInfo info : infos) {

				}

				openInChooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Parcelable[]{textIntent});
				startActivity(openInChooser);

				// The above is a start. Want to use queryIntentActivities to fine tune it better
				// (like http://stackoverflow.com/questions/20889968/android-image-share-intent-like-androidify-app)
				*/

				break;
			}
			case FEEDBACK: {
				String subject;
				if (mCurrentUser == null) {
					subject = BuildConfig.FEEDBACK_SUBJECT_VISITOR;
				} else {
					String fullName = mCurrentUser.getFullName();
					if (TextUtils.isEmpty(fullName)) {
						subject = BuildConfig.FEEDBACK_SUBJECT_USER;
					} else {
						subject = String.format(BuildConfig.FEEDBACK_SUBJECT, fullName);
					}
				}
				Intent intent = new Intent(Intent.ACTION_SEND);
				intent.setType(EMAIL_INTENT_TYPE);
				intent.putExtra(Intent.EXTRA_EMAIL, BuildConfig.FEEDBACK_EMAILS);
				intent.putExtra(Intent.EXTRA_SUBJECT, subject);
				intent.putExtra(Intent.EXTRA_TEXT, "");
				startActivity(intent);
				break;
			}
		}
		return false;
	}

	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		PreferenceType type = PreferenceType.fromKey(preference.getKey());
		switch (type) {
			case SHARE_APP: {

				return true;
			}
			default: {
				return false;
			}
		}
	}

	public static class ShareAppDialogFragment extends DialogFragment {

		private static final String FRAGMENT_TAG = ShareAppDialogFragment.class.getName();

		@NonNull
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			return new AlertDialog.Builder(getActivity())
					.setTitle("Share App")
					.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, android.R.id.text1, new String[]{"Email", "SMS"}), null)
					.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							Fragment targetFragment = getTargetFragment();
							if (targetFragment != null) {
								targetFragment.onActivityResult(getTargetRequestCode(), Activity.RESULT_CANCELED, null);
							}
						}
					})
					.show();
		}
	}
}
