package com.citymaps.mobile.android.model.vo;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import com.android.volley.Response;
import com.citymaps.mobile.android.app.SessionManager;
import com.citymaps.mobile.android.config.Endpoint;
import com.citymaps.mobile.android.model.GetGsonRequest;
import com.citymaps.mobile.android.model.ResultWrapperV2;

public class Version extends ResultWrapperV2 {

	public static final Parcelable.Creator<Version> CREATOR = new Parcelable.Creator<Version>() {
		@Override
		public Version createFromParcel(Parcel in) {
			return new Version(in);
		}

		@Override
		public Version[] newArray(int size) {
			return new Version[size];
		}
	};

	public Version() {
	}

	private Version(Parcel in) {
		super(in);
	}

	public static class GetRequest extends GetGsonRequest<Version> {

		public GetRequest(Context context, Response.Listener<Version> listener, Response.ErrorListener errorListener) {
			super(SessionManager.getInstance(context).getEnvironment().buildUrlString(Endpoint.Type.VERSION),
					Version.class, null, listener, errorListener);
		}
	}
}
