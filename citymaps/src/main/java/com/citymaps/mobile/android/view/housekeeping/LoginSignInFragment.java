package com.citymaps.mobile.android.view.housekeeping;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.*;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.citymaps.mobile.android.BuildConfig;
import com.citymaps.mobile.android.R;
import com.citymaps.mobile.android.app.VolleyManager;
import com.citymaps.mobile.android.model.request.UserRequest;
import com.citymaps.mobile.android.model.vo.User;
import com.citymaps.mobile.android.util.LogEx;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LoginSignInFragment.OnSignInListener} interface
 * to handle interaction events.
 * Use the {@link LoginSignInFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class LoginSignInFragment extends Fragment
		implements TextView.OnEditorActionListener, View.OnClickListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

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
			mListener = (OnSignInListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnSignInListener");
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
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		switch (id) {
			case R.id.action_submit:
				signIn();
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

	protected void signIn() {
		// Validate input
		String username = mUsernameView.getText().toString();
		if (TextUtils.isEmpty(username)) {
			CharSequence message = getString(R.string.error_login_missing_username);
			LoginErrorDialogFragment fragment =
					LoginErrorDialogFragment.newInstance(getActivity().getTitle(), message);
			fragment.show(getFragmentManager(), LoginErrorDialogFragment.FRAGMENT_TAG);
			mUsernameView.requestFocus();
			return;
		}

		String password = mPasswordView.getText().toString();
		if (TextUtils.isEmpty(password)) {
			CharSequence message = getString(R.string.error_login_missing_password);
			LoginErrorDialogFragment fragment =
					LoginErrorDialogFragment.newInstance(getActivity().getTitle(), message);
			fragment.show(getFragmentManager(), LoginErrorDialogFragment.FRAGMENT_TAG);
			mPasswordView.requestFocus();
			return;
		}

		int length = password.length();
		if (length < BuildConfig.PASSWORD_MIN_LENGTH || length > BuildConfig.PASSWORD_MAX_LENGTH) {
			CharSequence message = getString(R.string.error_login_password_length_message,
					BuildConfig.PASSWORD_MIN_LENGTH, BuildConfig.PASSWORD_MAX_LENGTH);
			LoginErrorDialogFragment fragment =
					LoginErrorDialogFragment.newInstance(getActivity().getTitle(), message);
			fragment.show(getFragmentManager(), LoginErrorDialogFragment.FRAGMENT_TAG);
			mPasswordView.requestFocus();
			return;
		}

		LogEx.d();
		// Try to sign in
		UserRequest loginRequest = UserRequest.newLoginRequest(getActivity(),
				username, password, new Response.Listener<User>() {
					@Override
					public void onResponse(User response) {
						LogEx.d("Logged In!!!!");
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						LogEx.d("Error: " + error.getMessage());
					}
				});
		VolleyManager.getInstance(getActivity()).getRequestQueue().add(loginRequest);
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
    public interface OnSignInListener {
		public void onSignInCreateAccount();
		public void onSignInResetPassword();
    }
}
