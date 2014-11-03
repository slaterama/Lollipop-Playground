package com.citymaps.mobile.android.map;

import android.os.Parcel;
import android.os.Parcelable;
import com.citymaps.citymapsengine.LonLat;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * A Parcelable version of {@link LonLat}.
 */
public class ParcelableLonLat extends LonLat
		implements Parcelable {

	public static final Creator<ParcelableLonLat> CREATOR = new Creator<ParcelableLonLat>() {
		@Override
		public ParcelableLonLat createFromParcel(Parcel source) {
			return new ParcelableLonLat(source);
		}

		@Override
		public ParcelableLonLat[] newArray(int size) {
			return new ParcelableLonLat[size];
		}
	};

	public static String lonLatToString(LonLat lonLat) {
		if (lonLat == null || lonLat instanceof ParcelableLonLat) {
			return String.valueOf(lonLat);
		} else {
			return new ToStringBuilder(lonLat)
					.append("longitude", lonLat.longitude)
					.append("latitude", lonLat.latitude)
					.toString();
		}
	}

	public ParcelableLonLat() {
		super();
	}

	public ParcelableLonLat(double lon, double lat) {
		super(lon, lat);
	}

	public ParcelableLonLat(LonLat other) {
		super(other);
	}

	private ParcelableLonLat(Parcel in) {
		longitude = in.readDouble();
		latitude = in.readDouble();
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel out, int flags) {
		out.writeDouble(longitude);
		out.writeDouble(latitude);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
