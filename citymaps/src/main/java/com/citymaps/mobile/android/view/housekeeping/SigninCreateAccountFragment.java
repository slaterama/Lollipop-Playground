package com.citymaps.mobile.android.view.housekeeping;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.Spannable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import com.android.volley.VolleyError;
import com.citymaps.mobile.android.R;
import com.citymaps.mobile.android.app.SessionManager;
import com.citymaps.mobile.android.app.VolleyManager;
import com.citymaps.mobile.android.model.ThirdParty;
import com.citymaps.mobile.android.model.ThirdPartyUser;
import com.citymaps.mobile.android.model.User;
import com.citymaps.mobile.android.model.request.UserRequest;
import com.citymaps.mobile.android.model.request.VolleyCallbacks;
import com.citymaps.mobile.android.util.CommonUtils;
import com.citymaps.mobile.android.util.SharedPrefUtils;
import com.citymaps.mobile.android.util.Validator;
import com.citymaps.mobile.android.util.ViewUtils;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SigninCreateAccountFragment.OnCreateAccountListener} interface
 * to handle interaction events.
 * Use the {@link SigninCreateAccountFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SigninCreateAccountFragment extends FormFragment
		implements VolleyCallbacks<User> {

	private static final int REQUEST_CODE_USE_THIRD_PARTY_INFO = 0;

	private static final String ARG_THIRD_PARTY_USER = "thirdPartyUser";

	private OnCreateAccountListener mListener;

	private EditText mFirstNameView;
	private EditText mLastNameView;
	private EditText mUsernameView;
	private EditText mEmailView;
	private EditText mPasswordView;
	private EditText mConfirmPasswordView;

	/**
	 * Use this factory method to create a new instance of
	 * this fragment using the provided parameters.
	 *
	 * @return A new instance of fragment LoginCreateAccountFragment.
	 */
	// TODO: Rename and change types and number of parameters
	public static SigninCreateAccountFragment newInstance() {
		SigninCreateAccountFragment fragment = new SigninCreateAccountFragment();
		return fragment;
	}

	/**
	 * @param thirdPartyUser
	 * @return
	 */
	public static SigninCreateAccountFragment newInstance(ThirdPartyUser thirdPartyUser) {
		SigninCreateAccountFragment fragment = new SigninCreateAccountFragment();
		Bundle args = new Bundle();
		args.putParcelable(ARG_THIRD_PARTY_USER, thirdPartyUser);
		fragment.setArguments(args);
		return fragment;
	}

	private ThirdPartyUser mThirdPartyUser;

	public SigninCreateAccountFragment() {
		// Required empty public constructor
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mListener = (OnCreateAccountListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnCreateAccountListener");
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Fragment fragment = getFragmentManager().findFragmentByTag(UseThirdPartyInfoDialogFragment.FRAGMENT_TAG);
		if (fragment != null) {
			fragment.setTargetFragment(this, REQUEST_CODE_USE_THIRD_PARTY_INFO);
		}

		if (getArguments() != null) {
			mThirdPartyUser = getArguments().getParcelable(ARG_THIRD_PARTY_USER);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_signin_create_account, container, false);
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		TextView disclaimerView = (TextView) view.findViewById(R.id.signin_create_account_disclaimer);
		disclaimerView.setText(Html.fromHtml(getString(R.string.signin_create_account_disclaimer)));
		Spannable spannable = (Spannable) Html.fromHtml(getString(R.string.signin_create_account_disclaimer));
		ViewUtils.setSpannableText(disclaimerView, spannable);

		mFirstNameView = (EditText) view.findViewById(R.id.signin_create_account_first_name);
		mLastNameView = (EditText) view.findViewById(R.id.signin_create_account_last_name);
		mUsernameView = (EditText) view.findViewById(R.id.signin_create_account_username);
		mEmailView = (EditText) view.findViewById(R.id.signin_create_account_email);
		mPasswordView = (EditText) view.findViewById(R.id.signin_create_account_password);
		mConfirmPasswordView = (EditText) view.findViewById(R.id.signin_create_account_confirm_password);
		mConfirmPasswordView.setOnEditorActionListener(this);

		if (savedInstanceState == null) {
			if (mThirdPartyUser != null) {
				if (getFragmentManager().findFragmentByTag(UseThirdPartyInfoDialogFragment.FRAGMENT_TAG) == null) {
					UseThirdPartyInfoDialogFragment fragment = UseThirdPartyInfoDialogFragment.newInstance(mThirdPartyUser);
					fragment.setTargetFragment(this, REQUEST_CODE_USE_THIRD_PARTY_INFO);
					fragment.show(getFragmentManager(), UseThirdPartyInfoDialogFragment.FRAGMENT_TAG);
				}
			}
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		getActivity().setTitle(R.string.signin_create_account_activity_title);
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mListener = null;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
			case REQUEST_CODE_USE_THIRD_PARTY_INFO:
				if (resultCode == Activity.RESULT_OK && mThirdPartyUser != null) {
					mFirstNameView.setText(mThirdPartyUser.getFirstName());
					mLastNameView.setText(mThirdPartyUser.getLastName());
					mUsernameView.setText(mThirdPartyUser.getUsername());
					mEmailView.setText(mThirdPartyUser.getEmail());
				}
				break;
			default:
				super.onActivityResult(requestCode, resultCode, data);
		}
	}

	@Override
	protected boolean validateForm() {
		if (!processInput(mFirstNameView.getText(), Validator.FIRST_NAME, true, true)) {
			return false;
		}

		if (!processInput(mLastNameView.getText(), Validator.LAST_NAME, true, true)) {
			return false;
		}

		if (!processInput(mUsernameView.getText(), Validator.USERNAME, true, false)) {
			return false;
		}

		if (!processInput(mEmailView.getText(), Validator.EMAIL, true, true)) {
			return false;
		}

		if (!processInput(mPasswordView.getText(), Validator.PASSWORD, true, false)) {
			return false;
		}

		if (!processInput(mPasswordView.getText(), mConfirmPasswordView.getText(), Validator.PASSWORD)) {
			return false;
		}

		return true;
	}

	@Override
	protected void onSubmitForm() {
		if (CommonUtils.notifyIfNoNetwork(getActivity())) {
			return;
		}
		String firstName = mFirstNameView.getText().toString();
		String lastName = mLastNameView.getText().toString();
		String username = mUsernameView.getText().toString();
		String email = mEmailView.getText().toString();
		String password = mPasswordView.getText().toString();
		ThirdParty thirdParty = (mThirdPartyUser == null ? null : mThirdPartyUser.getThirdParty());
		String thirdPartyId = (mThirdPartyUser == null ? null : mThirdPartyUser.getId());
		String thirdPartyToken = (mThirdPartyUser == null ? null : mThirdPartyUser.getToken());
		String thirdPartyAvatarUrl = (mThirdPartyUser == null ? null : mThirdPartyUser.getAvatarUrl());
		UserRequest registerRequest = UserRequest.newRegisterRequest(getActivity(), username, password,
				firstName, lastName, email, thirdParty, thirdPartyId, thirdPartyToken, thirdPartyAvatarUrl, this, this);
		VolleyManager.getInstance(getActivity()).getRequestQueue().add(registerRequest);
	}

	@Override
	public void onResponse(User response) {
		super.onResponse(response);
		SessionManager.getInstance(getActivity()).setCurrentUser(response);

		if (mThirdPartyUser != null) {
			// Capture third party token
			SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
			ThirdParty thirdParty = mThirdPartyUser.getThirdParty();
			SharedPrefUtils.putString(sp.edit(), thirdParty.getPreference(), mThirdPartyUser.getToken()).apply();
		}

		if (mListener != null) {
			mListener.onCreateAccountSuccess(response);
		}
	}

	@Override
	public void onErrorResponse(VolleyError error) {
		super.onErrorResponse(error);
		CommonUtils.showSimpleDialogFragment(getFragmentManager(),
				getActivity().getTitle(), error.getLocalizedMessage());
	}

	public static class UseThirdPartyInfoDialogFragment extends DialogFragment
			implements DialogInterface.OnClickListener {
		public static final String FRAGMENT_TAG = "useThirdPartyInfo";

		public static final String ARG_THIRD_PARTY_USER = "thirdPartyUser";

		public static UseThirdPartyInfoDialogFragment newInstance(ThirdPartyUser thirdPartyUser) {
			UseThirdPartyInfoDialogFragment fragment = new UseThirdPartyInfoDialogFragment();
			Bundle args = new Bundle();
			args.putParcelable(ARG_THIRD_PARTY_USER, thirdPartyUser);
			fragment.setArguments(args);
			return fragment;
		}

		@Override
		public void onClick(DialogInterface dialog, int which) {
			Fragment targetFragment = getTargetFragment();
			if (targetFragment != null) {
				switch (which) {
					case DialogInterface.BUTTON_POSITIVE:
						targetFragment.onActivityResult(REQUEST_CODE_USE_THIRD_PARTY_INFO, Activity.RESULT_OK, null);
						break;
					case DialogInterface.BUTTON_NEGATIVE:
					default:
						targetFragment.onActivityResult(REQUEST_CODE_USE_THIRD_PARTY_INFO, Activity.RESULT_CANCELED, null);
				}
			}
		}

		@NonNull
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			ThirdPartyUser thirdPartyUser = getArguments().getParcelable(ARG_THIRD_PARTY_USER);
			return new AlertDialog.Builder(getActivity())
					.setTitle(getActivity().getTitle())
					.setMessage(getString(R.string.signin_create_account_use_third_party_info, thirdPartyUser.getThirdParty().getProperName()))
					.setPositiveButton(android.R.string.yes, this)
					.setNegativeButton(android.R.string.no, this)
					.show();
		}
	}

	/**
	 * This interface must be implemented by activities that contain this
	 * fragment to allow an interaction in this fragment to be communicated
	 * to the activity and potentially other fragments contained in that
	 * activity.
	 * <p/>
	 * See the Android Training lesson <a href=
	 * "http://developer.android.com/training/basics/fragments/communicating.html"
	 * >Communicating with Other Fragments</a> for more information.
	 */
	public interface OnCreateAccountListener {
		// TODO: Update argument type and name
		public void onCreateAccountSuccess(User currentUser);
	}
}
