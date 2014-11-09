package com.citymaps.mobile.android.view.update;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import com.citymaps.mobile.android.BuildConfig;
import com.citymaps.mobile.android.R;
import com.citymaps.mobile.android.model.vo.Config;
import com.citymaps.mobile.android.util.SharedPreferenceUtils;
import com.citymaps.mobile.android.util.UpdateUtils;

/**
 * A simple {@link DialogFragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SoftUpdateDialogFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SoftUpdateDialogFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class SoftUpdateDialogFragment extends DialogFragment {

	public static final String FRAGMENT_TAG = "softUpgrade";

	/**
	 * Use this factory method to create a new instance of
	 * this fragment using the provided parameters.
	 *
	 * @param config The {@link Config} instance.
	 * @return A new instance of fragment SoftUpgradeDialogFragment.
	 */
	public static SoftUpdateDialogFragment newInstance(Config config) {
		SoftUpdateDialogFragment fragment = new SoftUpdateDialogFragment();
		Bundle args = new Bundle();
		args.putParcelable(ARG_CONFIG, config);
		fragment.setArguments(args);
		return fragment;
	}

    private static final String ARG_CONFIG = "config";

    private Config mConfig;

//    private OnFragmentInteractionListener mListener;

	private OnClickListener mOnClickListener = new OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			SharedPreferences sp = SharedPreferenceUtils.getConfigSharedPreferences(getActivity());
			SharedPreferenceUtils.putProcessedAction(sp, which);
			SharedPreferenceUtils.putProcessedTimestamp(sp, System.currentTimeMillis());
			switch (which) {
				case DialogInterface.BUTTON_POSITIVE: // Update
					UpdateUtils.goToPlayStore(getActivity());
					break;
				case DialogInterface.BUTTON_NEGATIVE: // Skip
					// No additional action needed
					break;
				case DialogInterface.BUTTON_NEUTRAL: // Later
					// No additional action needed
					break;
			}
		}
	};

    public SoftUpdateDialogFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mConfig = getArguments().getParcelable(ARG_CONFIG);
        }
    }

	@NonNull
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		return new AlertDialog.Builder(getActivity())
				.setTitle(R.string.update_soft_dialog_title)
				.setMessage(R.string.update_soft_dialog_message)
				.setPositiveButton(R.string.update_soft_dialog_button_update, mOnClickListener)
				.setNegativeButton(R.string.update_soft_dialog_button_skip, mOnClickListener)
				.setNeutralButton(R.string.update_soft_dialog_button_later, mOnClickListener)
				.create();
	}

	@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
		/*
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
        */
    }

    @Override
    public void onDetach() {
        super.onDetach();
//        mListener = null;
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
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}
