package com.citymaps.mobile.android.model.vo;

import android.os.Parcel;
import android.os.Parcelable;
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
}
