package com.citymaps.mobile.android.model.request;

import android.content.Context;
import android.net.Uri;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.citymaps.mobile.android.BuildConfig;
import com.citymaps.mobile.android.app.SessionManager;
import com.citymaps.mobile.android.config.Endpoint;
import com.citymaps.mobile.android.config.Environment;
import com.citymaps.mobile.android.model.FoursquarePhoto;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

public class FoursquarePhotosRequest extends GsonRequest<List<FoursquarePhoto>> {

	protected static final String GROUP = "group";
	protected static final String OFFSET = "offset";
	protected static final String LIMIT = "limit";

	public static final Type TYPE = new TypeToken<List<FoursquarePhoto>>() {}.getType();

	protected static FoursquarePhotosRequest getFoursquarePhotosRequest(Context context, String foursquareId, boolean includeLimit, int limit,
																		boolean includeOffset, int offset, Group group,
																		Response.Listener<List<FoursquarePhoto>> listener, Response.ErrorListener errorListener) {
		Environment environment = SessionManager.getInstance(context).getEnvironment();
		String urlString = environment.buildUrlString(Endpoint.Type.FOURSQUARE_GET_PHOTOS,
				foursquareId, BuildConfig.FOURSQUARE_VERSION, BuildConfig.FOURSQUARE_API_CLIENT_ID, BuildConfig.FOURSQUARE_API_CLIENT_SECRET);
		Uri.Builder builder = Uri.parse(urlString).buildUpon();
		if (includeLimit) {
			builder.appendQueryParameter(LIMIT, String.valueOf(limit));
		}
		if (includeOffset) {
			builder.appendQueryParameter(OFFSET, String.valueOf(offset));
		}
		if (group != null && group != Group.NONE) {
			builder.appendQueryParameter(GROUP, group.toString());
		}
		return new FoursquarePhotosRequest(Method.GET, builder.toString(), null, null, listener, errorListener);
	}

	public static FoursquarePhotosRequest getFoursquarePhotosRequest(Context context, String foursquareId, int limit, int offset,
																	 Response.Listener<List<FoursquarePhoto>> listener, Response.ErrorListener errorListener) {
		return getFoursquarePhotosRequest(context, foursquareId, true, limit, true, offset, null, listener, errorListener);
	}

	public static FoursquarePhotosRequest getFoursquarePhotosRequest(Context context, String foursquareId, int limit,
																	 Response.Listener<List<FoursquarePhoto>> listener, Response.ErrorListener errorListener) {
		return getFoursquarePhotosRequest(context, foursquareId, true, limit, false, 0, null, listener, errorListener);
	}

	public static FoursquarePhotosRequest getFoursquarePhotosRequest(Context context, String foursquareId,
																	 Response.Listener<List<FoursquarePhoto>> listener, Response.ErrorListener errorListener) {
		return getFoursquarePhotosRequest(context, foursquareId, false, 0, false, 0, null, listener, errorListener);
	}

	public FoursquarePhotosRequest(int method, String url, Map<String, String> headers, Map<String, String> params,
								   Response.Listener<List<FoursquarePhoto>> listener, Response.ErrorListener errorListener) {
		super(method, url, TYPE, headers, params, listener, errorListener);
	}

	@Override
	protected Response<List<FoursquarePhoto>> processParsedNetworkResponse(NetworkResponse response, JsonObject jsonObject) {
		try {
			PhotosWrapper result = getGson().fromJson(jsonObject, PhotosWrapper.class);
			List<FoursquarePhoto> items = result.mResponse.mPhotos.mItems;
			return Response.success(items, HttpHeaderParser.parseCacheHeaders(response));
		} catch (NullPointerException e) {
			return Response.error(new VolleyError(response));
		}
	}

	public static class PhotosWrapper {
		public static final String META = "meta";
		public static final String RESPONSE = "response";

		@SerializedName(META)
		private Meta mMeta;

		@SerializedName(RESPONSE)
		private Response mResponse;

		public static class Meta {
			public static final String CODE = "code";

			@SerializedName(CODE)
			private int mCode;
		}

		public static class Response {
			public static final String PHOTOS = "photos";

			@SerializedName(PHOTOS)
			private Photos mPhotos;

			public static class Photos {
				public static final String COUNT = "count";
				public static final String ITEMS = "items";
				public static final String DUPES_REMOVED = "dupesRemoved";

				@SerializedName(COUNT)
				private int mCount;

				@SerializedName(ITEMS)
				private List<FoursquarePhoto> mItems;

				@SerializedName(DUPES_REMOVED)
				private int mDupesRemoved;
			}
		}
	}

	public static enum Group {
		NONE(""),
		VENUE("venue"),
		CHECKIN("checkin");

		private String mValue;

		private Group(String value) {
			mValue = value;
		}

		@Override
		public String toString() {
			return mValue;
		}
	}
}
