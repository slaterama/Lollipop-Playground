package com.citymaps.mobile.android.view.housekeeping;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
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

	// TODO: Rename parameter arguments, choose names that match
	// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
	private static final String ARG_PARAM1 = "param1";
	private static final String ARG_PARAM2 = "param2";

	// TODO: Rename and change types of parameters
	private String mParam1;
	private String mParam2;

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
	 * @param param1 Parameter 1.
	 * @param param2 Parameter 2.
	 * @return A new instance of fragment LoginCreateAccountFragment.
	 */
	// TODO: Rename and change types and number of parameters
	public static LoginCreateAccountFragment newInstance(String param1, String param2) {
		LoginCreateAccountFragment fragment = new LoginCreateAccountFragment();
		Bundle args = new Bundle();
		args.putString(ARG_PARAM1, param1);
		args.putString(ARG_PARAM2, param2);
		fragment.setArguments(args);
		return fragment;
	}

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
		if (getArguments() != null) {
			mParam1 = getArguments().getString(ARG_PARAM1);
			mParam2 = getArguments().getString(ARG_PARAM2);
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
		UserRequest registerRequest = UserRequest.newRegisterRequest(getActivity(), username, password, firstName, lastName, email, this, this);
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
			String message = error.getLocalizedMessage();
			if (TextUtils.isEmpty(message)) {
				message = getString(R.string.error_message_generic);
			}
			LoginErrorDialogFragment fragment =
					LoginErrorDialogFragment.newInstance(getActivity().getTitle(), message);
			fragment.show(getFragmentManager(), LoginErrorDialogFragment.FRAGMENT_TAG);
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
