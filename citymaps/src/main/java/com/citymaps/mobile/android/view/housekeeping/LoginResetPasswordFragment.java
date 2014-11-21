package com.citymaps.mobile.android.view.housekeeping;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;
import com.android.volley.NoConnectionError;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.citymaps.mobile.android.R;
import com.citymaps.mobile.android.app.VolleyManager;
import com.citymaps.mobile.android.model.User;
import com.citymaps.mobile.android.model.volley.UserRequest;
import com.citymaps.mobile.android.util.CommonUtils;
import com.citymaps.mobile.android.util.Validator;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LoginResetPasswordFragment.OnResetPasswordListener} interface
 * to handle interaction events.
 * Use the {@link LoginResetPasswordFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginResetPasswordFragment extends LoginFragment {

	// TODO: Rename parameter arguments, choose names that match
	// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
	private static final String ARG_PARAM1 = "param1";
	private static final String ARG_PARAM2 = "param2";

	// TODO: Rename and change types of parameters
	private String mParam1;
	private String mParam2;

	private OnResetPasswordListener mListener;

	private EditText mEmailView;
	private EditText mConfirmEmailView;

	/**
	 * Use this factory method to create a new instance of
	 * this fragment using the provided parameters.
	 *
	 * @param param1 Parameter 1.
	 * @param param2 Parameter 2.
	 * @return A new instance of fragment LoginResetPasswordFragment.
	 */
	// TODO: Rename and change types and number of parameters
	public static LoginResetPasswordFragment newInstance(String param1, String param2) {
		LoginResetPasswordFragment fragment = new LoginResetPasswordFragment();
		Bundle args = new Bundle();
		args.putString(ARG_PARAM1, param1);
		args.putString(ARG_PARAM2, param2);
		fragment.setArguments(args);
		return fragment;
	}

	public LoginResetPasswordFragment() {
		// Required empty public constructor
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mListener = (OnResetPasswordListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnResetPasswordListener");
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
		return inflater.inflate(R.layout.fragment_login_reset_password, container, false);
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mEmailView = (EditText) view.findViewById(R.id.login_reset_password_email);
		mConfirmEmailView = (EditText) view.findViewById(R.id.login_reset_password_confirm_email);
		mConfirmEmailView.setOnEditorActionListener(this);
	}

	@Override
	public void onResume() {
		super.onResume();
		getActivity().setTitle(R.string.login_reset_password_activity_title);
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mListener = null;
	}

	@Override
	protected boolean validateForm() {
		if (!processInput(mEmailView.getText(), Validator.EMAIL, true, true)) {
			return false;
		}

		if (!processInput(mEmailView.getText(), mConfirmEmailView.getText(), Validator.EMAIL)) {
			return false;
		}

		return true;
	}

	@Override
	protected void onSubmitForm() {
		if (CommonUtils.notifyIfNoNetwork(getActivity())) {
			return;
		}
		String email = mEmailView.getText().toString();
		UserRequest registerRequest = UserRequest.newResetPasswordRequest(getActivity(), email, this, this);
		VolleyManager.getInstance(getActivity()).getRequestQueue().add(registerRequest);
	}

	@Override
	public void onResponse(User response) {
		super.onResponse(response);
		if (response == null) {
			String message = getString(R.string.login_reset_password_failure);
			onErrorResponse(new VolleyError(message, new ServerError()));
		} else {
			String message = getString(R.string.login_reset_password_success, response.getEmail());
			Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();

			if (mListener != null) {
				mListener.onResetPasswordSuccess();
			}
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
	public interface OnResetPasswordListener {
		public void onResetPasswordSuccess();
	}
}
