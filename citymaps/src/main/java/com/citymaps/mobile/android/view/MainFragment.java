package com.citymaps.mobile.android.view;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Resources;
import android.graphics.Outline;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.RelativeLayout;
import com.citymaps.citymapsengine.CitymapsMapView;
import com.citymaps.mobile.android.R;
import com.citymaps.mobile.android.map.MapViewService;
import com.citymaps.mobile.android.map.ParcelableMapPosition;
import com.citymaps.mobile.android.map.SimpleMapViewOwner;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MainFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainFragment extends Fragment {

	private static final String STATE_KEY_MAP_POSITION = "mapPosition";

	// TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

	/**
	 * Use this factory method to create a new instance of
	 * this fragment using the provided parameters.
	 *
	 * @param param1 Parameter 1.
	 * @param param2 Parameter 2.
	 * @return A new instance of fragment MainFragment.
	 */
	// TODO: Rename and change types and number of parameters
	public static MainFragment newInstance(String param1, String param2) {
		MainFragment fragment = new MainFragment();
		Bundle args = new Bundle();
		args.putString(ARG_PARAM1, param1);
		args.putString(ARG_PARAM2, param2);
		fragment.setArguments(args);
		return fragment;
	}

	// TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

	private Intent mMapViewServiceIntent;

	/*
	private Drawable mAppBarBackgroundDrawable;
	private int mAppBarBackgroundAlpha;
	*/

	private RelativeLayout mRelativeLayout;

	/*
	TODO Test
	private Toolbar mBottomToolbar;
	*/

	private MapViewService.MapViewBinder mMapViewBinder;

	private CitymapsMapView mMapView;

	private ParcelableMapPosition mMapPosition;

	private ServiceConnection mMapViewServiceConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			mMapViewBinder = (MapViewService.MapViewBinder) service;
			mMapViewBinder.registerMapViewContainer(mRelativeLayout, mMapViewOwner);
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			mMapViewBinder.unregisterMapViewContainer(mRelativeLayout);
			mMapViewBinder = null;
		}
	};

	private SimpleMapViewOwner mMapViewOwner = new SimpleMapViewOwner() {
		@Override
		public void onMapViewOwnershipGranted(ViewGroup parent, CitymapsMapView mapView) {
			mMapView = mapView;
			if (mMapPosition == null) {
				mMapPosition = new ParcelableMapPosition();
			}
			mMapView.setMapPosition(mMapPosition);
		}

		@Override
		public void onMapViewOwnershipRevoking(ViewGroup parent, CitymapsMapView mapView) {
			mMapPosition = captureMapPosition(mapView);
		}

		@Override
		public void onMapViewOwnershipRevoked(ViewGroup parent, CitymapsMapView mapView) {
			mMapView = null;
		}
	};

    public MainFragment() {
        // Required empty public constructor
    }

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mListener = (OnFragmentInteractionListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnFragmentInteractionListener");
		}

		mMapViewServiceIntent = new Intent(activity, MapViewService.class);
		getActivity().bindService(mMapViewServiceIntent, mMapViewServiceConnection, Context.BIND_AUTO_CREATE);
	}

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

		if (savedInstanceState == null) {
			mMapPosition = new ParcelableMapPosition();
		} else {
			mMapPosition = savedInstanceState.getParcelable(STATE_KEY_MAP_POSITION);
		}

		Activity activity = getActivity();

		/*
		Resources resources = getResources();
		mAppBarBackgroundDrawable = resources.getDrawable(R.drawable.ab_background);
		mAppBarBackgroundAlpha = resources.getInteger(R.integer.ab_alpha);
		mAppBarBackgroundDrawable.setAlpha(mAppBarBackgroundAlpha);
		if (activity instanceof ActionBarActivity) {
			((ActionBarActivity) activity).getSupportActionBar().setBackgroundDrawable(mAppBarBackgroundDrawable);
		}
		*/

		// Ensure map view service is running
		activity.startService(mMapViewServiceIntent);
	}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_main, container, false);
	}

	@TargetApi(Build.VERSION_CODES.L)
	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mRelativeLayout = (RelativeLayout) view.findViewById(R.id.mapcontainer);

		/*
		TODO Test
		mBottomToolbar = (Toolbar) view.findViewById(R.id.bottomtoolbar);
		ViewCompat.setBackground(mBottomToolbar, mAppBarBackgroundDrawable);

		mBottomToolbar.inflateMenu(R.menu.main);
		 */

		FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.explore_btn);
		fab.setOutlineProvider(new ViewOutlineProvider() {
			@Override
			public void getOutline(View view, Outline outline) {
				int diameter = getResources().getDimensionPixelSize(R.dimen.floating_action_button_size);
//				Outline outline = new Outline();
				outline.setOval(0, 0, diameter, diameter);
			}
		});
		fab.setClipToOutline(true);

		fab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(getActivity(), ExploreActivity.class));
			}
		});

	}

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onResume() {
		super.onResume();
		if (mMapView != null)
			mMapView.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
		if (mMapView != null)
			mMapView.onPause();
	}

	@Override
	public void onStop() {
		super.onStop();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mMapPosition = captureMapPosition(mMapView);
		outState.putParcelable(STATE_KEY_MAP_POSITION, mMapPosition);
	}

	@Override
	public void onDetach() {
		super.onDetach();
		if (mMapViewBinder != null) {
			mMapViewBinder.unregisterMapViewContainer(mRelativeLayout);
		}
		getActivity().unbindService(mMapViewServiceConnection);
		mListener = null;
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
		if (mMapView != null)
			mMapView.onLowMemory();
	}

	// TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

	private ParcelableMapPosition captureMapPosition(CitymapsMapView mapView) {
		return (mapView == null ? null
				: new ParcelableMapPosition(mapView.getCenter(), mapView.getZoom(), mapView.getRotation(), mapView.getTilt()));
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
