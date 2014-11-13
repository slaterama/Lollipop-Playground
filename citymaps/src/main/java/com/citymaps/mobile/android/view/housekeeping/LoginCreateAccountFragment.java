package com.citymaps.mobile.android.view.housekeeping;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.Spannable;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.*;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.NoConnectionError;
import com.android.volley.VolleyError;
import com.citymaps.mobile.android.BuildConfig;
import com.citymaps.mobile.android.R;
import com.citymaps.mobile.android.app.SessionManager;
import com.citymaps.mobile.android.app.VolleyManager;
import com.citymaps.mobile.android.model.User;
import com.citymaps.mobile.android.model.volley.UserRequest;
import com.citymaps.mobile.android.model.volley.VolleyCallbacks;
import com.citymaps.mobile.android.util.CitymapsPatterns;
import com.citymaps.mobile.android.util.ViewUtils;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LoginCreateAccountFragment.OnCreateAccountListener} interface
 * to handle interaction events.
 * Use the {@link LoginCreateAccountFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class LoginCreateAccountFragment extends Fragment
		implements TextView.OnEditorActionListener, VolleyCallbacks<User> {

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

	private Map<EditText, FieldValidator> mFieldValidatorMap;

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

		mFieldValidatorMap = new LinkedHashMap<EditText, FieldValidator>(5);
		mFieldValidatorMap.put(mFirstNameView, FieldValidator.FIRST_NAME);
		mFieldValidatorMap.put(mLastNameView, FieldValidator.LAST_NAME);
		mFieldValidatorMap.put(mUsernameView, FieldValidator.USERNAME);
		mFieldValidatorMap.put(mEmailView, FieldValidator.EMAIL);
		mFieldValidatorMap.put(mPasswordView, FieldValidator.PASSWORD);
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
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		switch (id) {
			case R.id.action_submit:
				createAccount();
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		if (actionId == EditorInfo.IME_ACTION_GO) {
			createAccount();
			return true;
		}
		return false;
	}

	@Override
	public void onResponse(User response) {
//		mActivity.setSupportProgressBarIndeterminateVisibility(false);

		SessionManager.getInstance(getActivity()).setCurrentUser(response);
		if (mListener != null) {
			mListener.onCreateAccountSuccess(response);
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

		// Make sure passwords match
		if (TextUtils.isEmpty(message)) {
			String password = mPasswordView.getText().toString();
			String confirmPassword = mConfirmPasswordView.getText().toString();
			if (!TextUtils.equals(password, confirmPassword)) {
				message = getString(R.string.error_login_passwords_do_not_match);
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

	private void createAccount() {
		if (!validateFields()) {
			return;
		}

		InputMethodManager manager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		manager.hideSoftInputFromWindow(mFirstNameView.getWindowToken(), 0);

		String firstName = mFirstNameView.getText().toString();
		String lastName = mLastNameView.getText().toString();
		String username = mUsernameView.getText().toString();
		String email = mEmailView.getText().toString();
		String password = mPasswordView.getText().toString();
		UserRequest registerRequest = UserRequest.newRegisterRequest(getActivity(), username, password, firstName, lastName, email, this, this);
		VolleyManager.getInstance(getActivity()).getRequestQueue().add(registerRequest);
	}

	/**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnCreateAccountListener {
        // TODO: Update argument type and name
		public void onCreateAccountSuccess(User currentUser);
    }

	private static enum FieldValidator {
		FIRST_NAME(CitymapsPatterns.NAME, R.string.error_login_enter_your_first_name,
				R.string.error_login_valid_first_name_message, BuildConfig.NAME_MIN_LENGTH, BuildConfig.NAME_MAX_LENGTH),
		LAST_NAME(CitymapsPatterns.NAME, R.string.error_login_enter_your_last_name,
				R.string.error_login_valid_last_name_message, BuildConfig.NAME_MIN_LENGTH, BuildConfig.NAME_MAX_LENGTH),
		USERNAME(CitymapsPatterns.USERNAME, R.string.error_login_enter_a_username,
				R.string.error_login_valid_username_message, BuildConfig.USERNAME_MIN_LENGTH, BuildConfig.USERNAME_MAX_LENGTH),
		EMAIL(Patterns.EMAIL_ADDRESS, R.string.error_login_enter_your_email,
				R.string.error_login_valid_email_message),
		PASSWORD(CitymapsPatterns.PASSWORD, R.string.error_login_enter_a_password,
				R.string.error_login_valid_password_message, BuildConfig.PASSWORD_MIN_LENGTH, BuildConfig.PASSWORD_MAX_LENGTH);

		private Pattern mPattern;
		private int mMissingFieldMessageResId;
		private int mInvalidFieldMessageResId;
		private Object[] mInvalidFieldMessageArgs;

		private FieldValidator(Pattern pattern, int missingFieldMessageResId, int invalidFieldMessageResId, Object... invalidFieldMessageArgs) {
			mPattern = pattern;
			mMissingFieldMessageResId = missingFieldMessageResId;
			mInvalidFieldMessageResId = invalidFieldMessageResId;
			mInvalidFieldMessageArgs = invalidFieldMessageArgs;
		}
	}
}
