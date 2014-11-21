package com.citymaps.mobile.android.view.housekeeping;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.android.volley.NoConnectionError;
import com.android.volley.VolleyError;
import com.citymaps.mobile.android.R;
import com.citymaps.mobile.android.app.SessionManager;
import com.citymaps.mobile.android.app.VolleyManager;
import com.citymaps.mobile.android.model.User;
import com.citymaps.mobile.android.model.volley.UserRequest;
import com.citymaps.mobile.android.util.CommonUtils;
import com.citymaps.mobile.android.util.Validator;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LoginSignInFragment.OnSignInListener} interface
 * to handle interaction events.
 * Use the {@link LoginSignInFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginSignInFragment extends LoginFragment
		implements View.OnClickListener {

	// TODO: Rename parameter arguments, choose names that match
	// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
	private static final String ARG_PARAM1 = "param1";
	private static final String ARG_PARAM2 = "param2";

	// TODO: Rename and change types of parameters
	private String mParam1;
	private String mParam2;

	private ActionBarActivity mActivity;
	private OnSignInListener mListener;

	private EditText mUsernameView;
	private EditText mPasswordView;

	/**
	 * Use this factory method to create a new instance of
	 * this fragment using the provided parameters.
	 *
	 * @param param1 Parameter 1.
	 * @param param2 Parameter 2.
	 * @return A new instance of fragment LoginSignInFragment.
	 */
	// TODO: Rename and change types and number of parameters
	public static LoginSignInFragment newInstance(String param1, String param2) {
		LoginSignInFragment fragment = new LoginSignInFragment();
		Bundle args = new Bundle();
		args.putString(ARG_PARAM1, param1);
		args.putString(ARG_PARAM2, param2);
		fragment.setArguments(args);
		return fragment;
	}

	public LoginSignInFragment() {
		// Required empty public constructor
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mActivity = (ActionBarActivity) activity;
			mListener = (OnSignInListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must extend ActionBarActivity and implement OnSignInListener");
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
		return inflater.inflate(R.layout.fragment_login_sign_in, container, false);
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mUsernameView = (EditText) view.findViewById(R.id.login_sign_in_username);
		mPasswordView = (EditText) view.findViewById(R.id.login_sign_in_password);
		mPasswordView.setOnEditorActionListener(this);
		Button createAccountBtn = (Button) view.findViewById(R.id.login_sign_in_create_account_button);
		createAccountBtn.setOnClickListener(this);
		Button resetPasswordBtn = (Button) view.findViewById(R.id.login_sign_in_reset_password_button);
		resetPasswordBtn.setOnClickListener(this);
	}

	@Override
	public void onResume() {
		super.onResume();
		getActivity().setTitle(R.string.login_sign_in_activity_title);
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mListener = null;
	}

	@Override
	protected boolean validateForm() {
		if (!processInput(mUsernameView.getText(), Validator.USERNAME, true, true)) {
			return false;
		}

		if (!processInput(mPasswordView.getText(), Validator.PASSWORD, true, true)) {
			return false;
		}

		return true;
	}

	@Override
	protected void onSubmitForm() {
		if (CommonUtils.notifyIfNoNetwork(getActivity())) {
			return;
		}
		String username = mUsernameView.getText().toString();
		String password = mPasswordView.getText().toString();
		UserRequest loginRequest = UserRequest.newLoginRequest(mActivity, username, password, this, this);
		VolleyManager.getInstance(getActivity()).getRequestQueue().add(loginRequest);
	}

	@Override
	public void onResponse(User response) {
		super.onResponse(response);
		SessionManager.getInstance(getActivity()).setCurrentUser(response);
		if (mListener != null) {
			mListener.onSignInSuccess(response);
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

	@Override
	public void onClick(View view) {
		int id = view.getId();
		switch (id) {
			case R.id.login_sign_in_create_account_button:
				if (mListener != null) {
					mListener.onSignInCreateAccount();
				}
				break;
			case R.id.login_sign_in_reset_password_button:
				if (mListener != null) {
					mListener.onSignInResetPassword();
				}
				break;
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
	public interface OnSignInListener {
		public void onSignInSuccess(User currentUser);

		public void onSignInCreateAccount();

		public void onSignInResetPassword();
	}
}
