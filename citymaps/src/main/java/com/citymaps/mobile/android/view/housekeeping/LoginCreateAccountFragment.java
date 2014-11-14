package com.citymaps.mobile.android.view.housekeeping;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.Spannable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.NoConnectionError;
import com.android.volley.VolleyError;
import com.citymaps.mobile.android.R;
import com.citymaps.mobile.android.app.SessionManager;
import com.citymaps.mobile.android.app.VolleyManager;
import com.citymaps.mobile.android.model.ThirdParty;
import com.citymaps.mobile.android.model.User;
import com.citymaps.mobile.android.model.volley.UserRequest;
import com.citymaps.mobile.android.model.volley.VolleyCallbacks;
import com.citymaps.mobile.android.util.Validator;
import com.citymaps.mobile.android.util.ViewUtils;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LoginCreateAccountFragment.OnCreateAccountListener} interface
 * to handle interaction events.
 * Use the {@link LoginCreateAccountFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginCreateAccountFragment extends LoginFragment
		implements VolleyCallbacks<User> {

	private static final int REQUEST_CODE_USE_THIRD_PARTY_INFO = 1001;

	private static final String ARG_THIRD_PARTY = "thirdParty";
	private static final String ARG_THIRD_PARTY_ID = "thirdPartyId";
	private static final String ARG_THIRD_PARTY_TOKEN = "thirdPartyToken";
	private static final String ARG_THIRD_PARTY_FIRST_NAME = "thirdPartyFirstName";
	private static final String ARG_THIRD_PARTY_LAST_NAME = "thirdPartyLastName";
	private static final String ARG_THIRD_PARTY_USERNAME = "thirdPartyUsername";
	private static final String ARG_THIRD_PARTY_EMAIL = "thirdPartyEmail";
	private static final String ARG_THIRD_PARTY_AVATAR_URL = "thirdPartyAvatarUrl";

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
	public static LoginCreateAccountFragment newInstance() {
		LoginCreateAccountFragment fragment = new LoginCreateAccountFragment();
		return fragment;
	}

	/**
	 *
	 * @param thirdParty
	 * @param thirdPartyId
	 * @param thirdPartyToken
	 * @param thirdPartyFirstName
	 * @param thirdPartyLastName
	 * @param thirdPartyUsername
	 * @param thirdPartyEmail
	 * @param thirdPartyAvatarUrl
	 * @return
	 */
	public static LoginCreateAccountFragment newInstance(ThirdParty thirdParty, String thirdPartyId, String thirdPartyToken,
														 String thirdPartyFirstName, String thirdPartyLastName,
														 String thirdPartyUsername, String thirdPartyEmail, String thirdPartyAvatarUrl) {
		LoginCreateAccountFragment fragment = new LoginCreateAccountFragment();
		Bundle args = new Bundle();
		args.putSerializable(ARG_THIRD_PARTY, thirdParty);
		args.putString(ARG_THIRD_PARTY_ID, thirdPartyId);
		args.putString(ARG_THIRD_PARTY_TOKEN, thirdPartyToken);
		args.putString(ARG_THIRD_PARTY_FIRST_NAME, thirdPartyFirstName);
		args.putString(ARG_THIRD_PARTY_LAST_NAME, thirdPartyLastName);
		args.putString(ARG_THIRD_PARTY_USERNAME, thirdPartyUsername);
		args.putString(ARG_THIRD_PARTY_EMAIL, thirdPartyEmail);
		args.putString(ARG_THIRD_PARTY_AVATAR_URL, thirdPartyAvatarUrl);
		fragment.setArguments(args);
		return fragment;
	}

	private ThirdParty mThirdParty;

	public LoginCreateAccountFragment() {
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
			mThirdParty = (ThirdParty) getArguments().getSerializable(ARG_THIRD_PARTY);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_login_create_account, container, false);
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		TextView disclaimerView = (TextView) view.findViewById(R.id.login_create_account_disclaimer);
		disclaimerView.setText(Html.fromHtml(getString(R.string.login_create_account_disclaimer)));
		Spannable spannable = (Spannable) Html.fromHtml(getString(R.string.login_create_account_disclaimer));
		ViewUtils.setSpannableText(disclaimerView, spannable);

		mFirstNameView = (EditText) view.findViewById(R.id.login_create_account_first_name);
		mLastNameView = (EditText) view.findViewById(R.id.login_create_account_last_name);
		mUsernameView = (EditText) view.findViewById(R.id.login_create_account_username);
		mEmailView = (EditText) view.findViewById(R.id.login_create_account_email);
		mPasswordView = (EditText) view.findViewById(R.id.login_create_account_password);
		mConfirmPasswordView = (EditText) view.findViewById(R.id.login_create_account_confirm_password);
		mConfirmPasswordView.setOnEditorActionListener(this);

		if (savedInstanceState == null) {
			if (mThirdParty != null) {
				if (getFragmentManager().findFragmentByTag(UseThirdPartyInfoDialogFragment.FRAGMENT_TAG) == null) {
					UseThirdPartyInfoDialogFragment fragment = UseThirdPartyInfoDialogFragment.newInstance(mThirdParty);
					fragment.setTargetFragment(this, REQUEST_CODE_USE_THIRD_PARTY_INFO);
					fragment.show(getFragmentManager(), UseThirdPartyInfoDialogFragment.FRAGMENT_TAG);
				}
			}
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		getActivity().setTitle(R.string.login_create_account_activity_title);
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
				if (resultCode == Activity.RESULT_OK) {
					Bundle args = getArguments();
					if (args != null) {
						mFirstNameView.setText(args.getString(ARG_THIRD_PARTY_FIRST_NAME, ""));
						mLastNameView.setText(args.getString(ARG_THIRD_PARTY_LAST_NAME, ""));
						mUsernameView.setText(args.getString(ARG_THIRD_PARTY_USERNAME, ""));
						mEmailView.setText(args.getString(ARG_THIRD_PARTY_EMAIL, ""));
					}
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
		String firstName = mFirstNameView.getText().toString();
		String lastName = mLastNameView.getText().toString();
		String username = mUsernameView.getText().toString();
		String email = mEmailView.getText().toString();
		String password = mPasswordView.getText().toString();
		String thirdPartyId = null;
		String thirdPartyToken = null;
		String thirdPartyAvatarUrl = null;
		if (mThirdParty != null && getArguments() != null) {
			thirdPartyId = getArguments().getString(ARG_THIRD_PARTY_ID);
			thirdPartyToken = getArguments().getString(ARG_THIRD_PARTY_TOKEN);
			thirdPartyAvatarUrl = getArguments().getString(ARG_THIRD_PARTY_AVATAR_URL);
		}
		UserRequest registerRequest = UserRequest.newRegisterRequest(getActivity(), username, password,
				firstName, lastName, email, mThirdParty, thirdPartyId, thirdPartyToken, thirdPartyAvatarUrl, this, this);
		VolleyManager.getInstance(getActivity()).getRequestQueue().add(registerRequest);
	}

	@Override
	public void onResponse(User response) {
		super.onResponse(response);
		SessionManager.getInstance(getActivity()).setCurrentUser(response);
		if (mListener != null) {
			mListener.onCreateAccountSuccess(response);
		}
	}

	@Override
	public void onErrorResponse(VolleyError error) {
		super.onErrorResponse(error);
		if (error instanceof NoConnectionError) {
			Toast.makeText(getActivity(), R.string.error_message_no_connection, Toast.LENGTH_SHORT).show();
		} else {

			// TODO Why is localized message empty here?

			String message = error.getLocalizedMessage();
			if (TextUtils.isEmpty(message)) {
				message = getString(R.string.error_message_generic);
			}
			LoginErrorDialogFragment fragment =
					LoginErrorDialogFragment.newInstance(getActivity().getTitle(), message);
			fragment.show(getFragmentManager(), LoginErrorDialogFragment.FRAGMENT_TAG);
		}
	}

	public static class UseThirdPartyInfoDialogFragment extends DialogFragment
			implements DialogInterface.OnClickListener {
		public static final String FRAGMENT_TAG = "useThirdPartyInfo";

		public static final String ARG_THIRD_PARTY = "thirdParty";

		public static UseThirdPartyInfoDialogFragment newInstance(ThirdParty thirdParty) {
			UseThirdPartyInfoDialogFragment fragment = new UseThirdPartyInfoDialogFragment();
			Bundle args = new Bundle();
			args.putSerializable(ARG_THIRD_PARTY, thirdParty);
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
			ThirdParty thirdParty = (ThirdParty) getArguments().getSerializable(ARG_THIRD_PARTY);
			return new AlertDialog.Builder(getActivity())
					.setTitle(getActivity().getTitle())
					.setMessage(getString(R.string.login_create_account_use_third_party_info, thirdParty.getProperName()))
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
