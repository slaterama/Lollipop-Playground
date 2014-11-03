package com.citymaps.mobile.android.map;

import android.os.Parcel;
import android.os.Parcelable;
import com.citymaps.citymapsengine.LonLat;
import com.citymaps.citymapsengine.MapPosition;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class ParcelableMapPosition extends MapPosition
		implements Parcelable {

	public static final LonLat LON_LAT_NEW_YORK = new LonLat(-74.0064, 40.7142);

	public static final float DEFAULT_ZOOM = 15;
	public static final LonLat DEFAULT_LON_LAT = LON_LAT_NEW_YORK;
	public static final float DEFAULT_ORIENTATION = 0;
	public static final double DEFAULT_TILT = 0;

	public static final Creator<ParcelableMapPosition> CREATOR = new Creator<ParcelableMapPosition>() {
		@Override
		public ParcelableMapPosition createFromParcel(Parcel source) {
			return new ParcelableMapPosition(source);
		}

		@Override
		public ParcelableMapPosition[] newArray(int size) {
			return new ParcelableMapPosition[size];
		}
	};

	public ParcelableMapPosition() {
		super(DEFAULT_LON_LAT, DEFAULT_ZOOM, DEFAULT_ORIENTATION, DEFAULT_TILT);
	}

	public ParcelableMapPosition(LonLat center, float zoom, double orientation, double tilt) {
		super(center, zoom, orientation, tilt);
	}

	private ParcelableMapPosition(Parcel in) {
		super(
				in.readByte() == 0
						? new LonLat(in.readDouble(), in.readDouble())
						: (ParcelableLonLat) in.readParcelable(ParcelableLonLat.class.getClassLoader()),
				in.readFloat(),
				in.readDouble(),
				in.readDouble()
		);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel out, int flags) {
		if (center instanceof ParcelableLonLat) {
			out.writeByte((byte) 1);
			out.writeParcelable((ParcelableLonLat) center, flags);
		} else {
			out.writeByte((byte) 0);
			out.writeDouble(center.longitude);
			out.writeDouble(center.latitude);
		}
		out.writeFloat(zoom);
		out.writeDouble(orientation);
		out.writeDouble(tilt);
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this)
				.append("center", ParcelableLonLat.lonLatToString(center))
				.append("zoom", zoom)
				.append("orientation", orientation)
				.append("tilt", tilt)
				.toString();
	}
}
