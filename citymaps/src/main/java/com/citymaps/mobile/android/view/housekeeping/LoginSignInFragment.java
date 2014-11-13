package com.citymaps.mobile.android.view.housekeeping;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.view.*;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.NoConnectionError;
import com.android.volley.VolleyError;
import com.citymaps.mobile.android.BuildConfig;
import com.citymaps.mobile.android.R;
import com.citymaps.mobile.android.app.SessionManager;
import com.citymaps.mobile.android.app.VolleyManager;
import com.citymaps.mobile.android.modelnew.User;
import com.citymaps.mobile.android.modelnew.volley.UserRequest;
import com.citymaps.mobile.android.modelnew.volley.VolleyCallbacks;
import com.citymaps.mobile.android.util.CitymapsPatterns;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LoginSignInFragment.OnSignInListener} interface
 * to handle interaction events.
 * Use the {@link LoginSignInFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginSignInFragment extends Fragment
		implements TextView.OnEditorActionListener, View.OnClickListener, VolleyCallbacks<User> {

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

	private Map<EditText, FieldValidator> mFieldValidatorMap;

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
		setHasOptionsMenu(true);
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

		mFieldValidatorMap = new LinkedHashMap<EditText, FieldValidator>(2);
		mFieldValidatorMap.put(mUsernameView, FieldValidator.USERNAME);
		mFieldValidatorMap.put(mPasswordView, FieldValidator.PASSWORD);
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
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		switch (id) {
			case R.id.action_submit:
				signIn();
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		if (actionId == EditorInfo.IME_ACTION_GO) {
			signIn();
			return true;
		}
		return false;
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

	@Override
	public void onResponse(User response) {
//		mActivity.setSupportProgressBarIndeterminateVisibility(false);

		SessionManager.getInstance(getActivity()).setCurrentUser(response);
		if (mListener != null) {
			mListener.onSignInSuccess(response);
		}
	}

	@Override
	public void onErrorResponse(VolleyError error) {
//		mActivity.setSupportProgressBarIndeterminateVisibility(false);

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

	private boolean validateFields() {
		CharSequence message = null;

		Set<EditText> keySet = mFieldValidatorMap.keySet();
		for (EditText editText : keySet) {
			FieldValidator validator = mFieldValidatorMap.get(editText);
			String input = editText.getText().toString();
			if (TextUtils.isEmpty(input)) {
				message = getString(validator.mMissingFieldMessageResId);
				break;
			} else if (!validator.mPattern.matcher(input).matches()) {
				message = getString(validator.mInvalidFieldMessageResId, validator.mInvalidFieldMessageArgs);
				break;
			}
		}

		if (!TextUtils.isEmpty(message)) {
			LoginErrorDialogFragment fragment =
					LoginErrorDialogFragment.newInstance(getActivity().getTitle(), message);
			fragment.show(getFragmentManager(), LoginErrorDialogFragment.FRAGMENT_TAG);
			return false;
		}

		return true;
	}

	protected void signIn() {
		if (!validateFields()) {
			return;
		}

		String username = mUsernameView.getText().toString();
		String password = mPasswordView.getText().toString();
		UserRequest loginRequest = UserRequest.newLoginRequest(mActivity, username, password, this, this);
		VolleyManager.getInstance(getActivity()).getRequestQueue().add(loginRequest);
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

private static enum FieldValidator {
	USERNAME(CitymapsPatterns.USERNAME, R.string.error_login_enter_your_username,
			R.string.error_login_valid_username_message, BuildConfig.USERNAME_MIN_LENGTH, BuildConfig.USERNAME_MAX_LENGTH),
	PASSWORD(CitymapsPatterns.PASSWORD, R.string.error_login_enter_your_password,
			R.string.error_login_valid_password_message, BuildConfig.PASSWORD_MIN_LENGTH, BuildConfig.PASSWORD_MAX_LENGTH);

	private Pattern mPattern;
	private int mMissingFieldMessageResId;
	private int mInvalidFieldMessageResId;
	private Object[] mInvalidFieldMessageArgs;

	private FieldValidator(Pattern pattern, int missingFieldMessageResId,
						   int invalidFieldMessageResId, Object... invalidFieldMessageArgs) {
		mPattern = pattern;
		mMissingFieldMessageResId = missingFieldMessageResId;
		mInvalidFieldMessageResId = invalidFieldMessageResId;
		mInvalidFieldMessageArgs = invalidFieldMessageArgs;
	}
}
}
