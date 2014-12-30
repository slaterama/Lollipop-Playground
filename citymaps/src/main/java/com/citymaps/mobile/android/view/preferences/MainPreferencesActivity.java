package com.citymaps.mobile.android.view.preferences;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.text.method.TransformationMethod;
import android.view.*;
import android.view.inputmethod.EditorInfo;
import android.widget.*;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.citymaps.mobile.android.BuildConfig;
import com.citymaps.mobile.android.R;
import com.citymaps.mobile.android.app.SessionManager;
import com.citymaps.mobile.android.app.TrackedActionBarActivity;
import com.citymaps.mobile.android.app.VolleyManager;
import com.citymaps.mobile.android.model.User;
import com.citymaps.mobile.android.model.request.UserRequest;
import com.citymaps.mobile.android.preference.PreferenceFragment;
import com.citymaps.mobile.android.util.*;
import com.citymaps.mobile.android.view.MainActivity;
import com.citymaps.mobile.android.view.housekeeping.SignoutDialogFragment;

public class MainPreferencesActivity extends TrackedActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// NOTE: As per https://code.google.com/p/android/issues/detail?id=78701,
		// without this line, using android.R.id.content as the container res id
		// won't work
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		if (savedInstanceState == null) {
			Fragment fragment = PreferencesFragment.newInstance();
			getSupportFragmentManager()
					.beginTransaction()
					.add(android.R.id.content, fragment, null)
					.commit();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_preferences, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if (id == R.id.action_settings) {
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	public static class PreferencesFragment extends PreferenceFragment
			implements Preference.OnPreferenceClickListener {

		private static final int REQUEST_CODE_DEVELOPER_PASSWORD = 0;

		private static final int REQUEST_CODE_SIGNOUT = 1;

		public static PreferencesFragment newInstance() {
			return new PreferencesFragment();
		}

		private SharedPreferences mSharedPreferences;

		private Preference mDeveloperModePreference;

		private boolean mDeveloperModeEnabled;

		private User mCurrentUser;

		private boolean mSignedIn;

		@Override
		public void onAttach(Activity activity) {
			super.onAttach(activity);
			mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
		}

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			DialogFragment fragment = (DialogFragment) getFragmentManager()
					.findFragmentByTag(DeveloperPasswordDialogFragment.FRAGMENT_TAG);
			if (fragment != null) {
				fragment.setTargetFragment(this, REQUEST_CODE_DEVELOPER_PASSWORD);
			}
		}

		@Override
		public void onViewCreated(final View view, Bundle savedInstanceState) {
			super.onViewCreated(view, savedInstanceState);

			addPreferencesFromResource(R.xml.preferences_general);

			mCurrentUser = SessionManager.getInstance(getActivity()).getCurrentUser();
			mSignedIn = (mCurrentUser != null);
			if (mSignedIn) {
				addPreferencesFromResource(R.xml.preferences_signed_in);
			} else {
				addPreferencesFromResource(R.xml.preferences_signed_out);
			}

			findPreference(Pref.SHARE_APP.getKey()).setOnPreferenceClickListener(this);
			findPreference(Pref.ADD_BUSINESS.getKey()).setOnPreferenceClickListener(this);

			mDeveloperModeEnabled = SharedPrefUtils.getBoolean(mSharedPreferences, Pref.DEVELOPER_MODE_ENABLED, false);
			mDeveloperModePreference = findPreference(Pref.DEVELOPER_MODE.getKey());
			if (!mDeveloperModeEnabled) {
				mDeveloperModePreference.setWidgetLayoutResource(R.layout.preference_widget_layout_button);
			}
			mDeveloperModePreference.setOnPreferenceClickListener(this);

			if (mSignedIn) {
				findPreference(Pref.SIGNOUT.getKey()).setOnPreferenceClickListener(this);
			} else {
				findPreference(Pref.SIGNIN.getKey()).setOnPreferenceClickListener(this);
			}
		}

		@Override
		public void onActivityResult(int requestCode, int resultCode, Intent data) {
			switch (requestCode) {
				case REQUEST_CODE_DEVELOPER_PASSWORD:
					if (resultCode == Activity.RESULT_OK) {
						mDeveloperModeEnabled = true;
						mDeveloperModePreference.setWidgetLayoutResource(0);

						Intent intent = new Intent(getActivity(), DeveloperPreferencesActivity.class);
						intent.putExtra(IntentUtils.EXTRA_DEVELOPER_PASSWORD_JUST_ENTERED, true);
						startActivity(intent);
					}
					break;
				case REQUEST_CODE_SIGNOUT:
					if (resultCode == FragmentActivity.RESULT_OK) {
						final FragmentActivity activity = getActivity();
						final SessionManager sessionManager = SessionManager.getInstance(activity);
						User currentUser = sessionManager.getCurrentUser();
						if (currentUser != null) {
							UserRequest request = UserRequest.newLogoutRequest(activity, currentUser.getId(),
									new Response.Listener<User>() {
										@Override
										public void onResponse(User response) {
											sessionManager.setCurrentUser(null);

											/*
											TODO Figure out Third Party token stuff
											// Clear third party tokens from shared preferences
											SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(activity);
											sp.edit().remove(Pref.FACEBOOK_TOKEN.getKey())
													.remove(Pref.GOOGLE_TOKEN.getKey()).apply();
											*/

											activity.setResult(MainActivity.RESULT_LOGOUT);
											activity.finish();
										}
									},
									new Response.ErrorListener() {
										@Override
										public void onErrorResponse(VolleyError error) {
											// TODO Error handling
										}
									});
							VolleyManager.getInstance(activity).getRequestQueue().add(request);
						}
					}
					break;
				default:
					super.onActivityResult(requestCode, resultCode, data);
			}
		}

		@Override
		public boolean onPreferenceClick(Preference preference) {
			Pref pref = Pref.fromKey(preference.getKey());
			switch (pref) {
				case SHARE_APP:
					ShareUtils.shareApp(getActivity());
					return true;
				case DEVELOPER_MODE:
					if (mDeveloperModeEnabled) {
						startActivity(new Intent(getActivity(), DeveloperPreferencesActivity.class));
					} else {
						DeveloperPasswordDialogFragment.newInstance(this, REQUEST_CODE_DEVELOPER_PASSWORD)
								.show(getFragmentManager(), DeveloperPasswordDialogFragment.FRAGMENT_TAG);
					}
					return true;
				case SIGNIN:
					FragmentActivity activity = getActivity();
					activity.setResult(MainActivity.RESULT_LOGIN);
					activity.finish();
					return true;
				case SIGNOUT:
					FragmentManager manager = getFragmentManager();
					if (manager.findFragmentByTag(SignoutDialogFragment.FRAGMENT_TAG) == null) {
						SignoutDialogFragment fragment = SignoutDialogFragment.newInstance();
						fragment.setTargetFragment(this, REQUEST_CODE_SIGNOUT);
						fragment.show(manager, SignoutDialogFragment.FRAGMENT_TAG);
					}
					return true;
				default:
					return false;
			}
		}
	}

	/**
	 * A {@link android.app.DialogFragment} that asks the user for the developer mode password.
	 */
	public static class DeveloperPasswordDialogFragment extends DialogFragment
			implements TextView.OnEditorActionListener, CompoundButton.OnCheckedChangeListener {

		public static final String FRAGMENT_TAG = DeveloperPasswordDialogFragment.class.getName();

		public static DeveloperPasswordDialogFragment newInstance(Fragment targetFragment, int requestCode) {
			DeveloperPasswordDialogFragment fragment = new DeveloperPasswordDialogFragment();
			fragment.setTargetFragment(targetFragment, requestCode);
			return fragment;
		}

		/**
		 * The {@link android.widget.EditText} in which the user types the
		 * developer mode password.
		 */
		private EditText mPasswordEditText;

		/**
		 * A {@link android.widget.CheckBox} that allows the user to see the password as they type.
		 */
		private CheckBox mShowPasswordCheckBox;

		/**
		 * A {@link android.text.method.TransformationMethod} that controls whether the user
		 * can see the password as they type.
		 */
		private TransformationMethod mPasswordTransformationMethod;

		/**
		 * The callback for preference-related requests.
		 */
		//private PreferencesListener mPreferencesListener;

		public DeveloperPasswordDialogFragment() {
			super();
			mPasswordTransformationMethod = new PasswordTransformationMethod();
		}

		/*
		@Override
		public void onAttach(Activity activity) {
			super.onAttach(activity);
			try {
				mPreferencesListener = (PreferencesListener) activity;
			} catch (ClassCastException e) {
				throw new ClassCastException(String.format("%s must implement %s.%s",
						activity.toString(), PreferencesActivity.class.getSimpleName(),
						PreferencesListener.class.getSimpleName()));
			}
		}
		*/

		/**
		 * Builds the developer mode password dialog.
		 */
		@NonNull
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			View view = View.inflate(getActivity(), R.layout.dialog_fragment_developer_password, null);
			mPasswordEditText = (EditText) view.findViewById(R.id.edittext_password);
			mPasswordEditText.setOnEditorActionListener(this);
			mShowPasswordCheckBox = (CheckBox) view.findViewById(R.id.checkbox_show_password);
			mShowPasswordCheckBox.setOnCheckedChangeListener(this);
			onCheckedChanged(mShowPasswordCheckBox, mShowPasswordCheckBox.isChecked());

			return new AlertDialog.Builder(getActivity())
					.setTitle(R.string.pref_developer_mode_dialog_title)
					.setView(view)
					.setNegativeButton(android.R.string.cancel, null)
					.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialogInterface, int which) {
							if (TextUtils.equals(mPasswordEditText.getText(), BuildConfig.DEVELOPER_PASSWORD)) {
								SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
								SharedPrefUtils.putBoolean(sp.edit(), Pref.DEVELOPER_MODE_ENABLED, true).apply();
								getTargetFragment().onActivityResult(
										getTargetRequestCode(), Activity.RESULT_OK, null);
							} else {
								new IncorrectPasswordDialogFragment().show(getFragmentManager(), null);
							}
						}
					})
					.create();
		}

		@Override
		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
			if (actionId == EditorInfo.IME_ACTION_GO) {
				((AlertDialog) getDialog()).getButton(DialogInterface.BUTTON_POSITIVE).performClick();
				return true;
			}
			return false;
		}

		/**
		 * Controls whether the user can see the password as they type.
		 */
		@Override
		public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
			mPasswordEditText.setTransformationMethod(b ? null : mPasswordTransformationMethod);
		}
	}

	/**
	 * A simple {@link android.app.DialogFragment} that informs the user that they entered an
	 * incorrect developer mode password.
	 */
	public static class IncorrectPasswordDialogFragment extends DialogFragment {
		@NonNull
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			return new AlertDialog.Builder(getActivity())
					.setTitle(R.string.pref_developer_mode_dialog_title)
					.setMessage(R.string.pref_developer_mode_incorrect_password)
					.setPositiveButton(android.R.string.ok, null)
					.create();
		}
	}
}
