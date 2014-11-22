package com.citymaps.mobile.android.view.housekeeping;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.citymaps.mobile.android.R;
import com.citymaps.mobile.android.model.User;
import com.citymaps.mobile.android.util.CommonUtils;
import com.citymaps.mobile.android.util.Validator;

public abstract class FormFragment extends Fragment
		implements TextView.OnEditorActionListener, Response.Listener<User>, Response.ErrorListener {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		switch (id) {
			case R.id.action_submit:
				processForm();
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		if (actionId == EditorInfo.IME_ACTION_GO) {
			processForm();
			return true;
		}
		return false;
	}

	protected void showActivityIndicator() {

	}

	protected void hideActivityIndicator() {

	}

	protected boolean processInput(CharSequence input, Validator validator, boolean required, boolean your) {
		if (!validator.validate(input, required)) {
			String message = validator.getMessage(getActivity(), input, required, your);
			CommonUtils.showSimpleDialogFragment(getFragmentManager(),
					getActivity().getTitle(), message);
			return false;
		}
		return true;
	}

	protected boolean processInput(CharSequence input1, CharSequence input2, Validator validator) {
		if (!validator.equals(input1, input2)) {
			String message = validator.getMessage(getActivity(), input1, input2);
			CommonUtils.showSimpleDialogFragment(getFragmentManager(),
					getActivity().getTitle(), message);
			return false;
		}
		return true;
	}

	protected void processForm() {
		if (!validateForm()) {
			return;
		}

		View view = getView();
		if (view != null) {
			InputMethodManager manager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
			manager.hideSoftInputFromWindow(view.getWindowToken(), 0);
		}
		showActivityIndicator();
		onSubmitForm();
	}

	protected boolean validateForm() {
		return true;
	}

	protected abstract void onSubmitForm();

	@Override
	public void onResponse(User response) {
		hideActivityIndicator();
	}

	@Override
	public void onErrorResponse(VolleyError error) {
		hideActivityIndicator();
	}
}
