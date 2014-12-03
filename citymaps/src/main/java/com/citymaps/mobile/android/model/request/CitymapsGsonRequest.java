package com.citymaps.mobile.android.model.request;

import android.text.TextUtils;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.citymaps.mobile.android.config.Api;
import com.citymaps.mobile.android.model.Deal;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.Map;

@SuppressWarnings("SpellCheckingInspection")
public class CitymapsGsonRequest<T> extends GsonRequest<T> {

	protected static final String MEMBER_NAME_CODE_V1 = "code";

	Api.Version mVersion;

	public CitymapsGsonRequest(Api.Version version, int method, String url, Class<T> clazz,
							   Map<String, String> headers, Map<String, String> params,
							   Response.Listener<T> listener, Response.ErrorListener errorListener) {
		super(method, url, clazz, headers, params, listener, errorListener);
		mVersion = version;
	}

	@Override
	protected VolleyError processParsedNetworkError(VolleyError volleyError, JsonObject jsonObject) {
		switch (mVersion) {
			case V1:
				// Not sure if this code is ever executed
				return super.processParsedNetworkError(volleyError, jsonObject);
			case V2:
			default:
				// Parse into an error result
				ResultError result = getGson().fromJson(jsonObject, ResultError.class);
				String message = result.getMessage();
				String reason = result.getReason();
				String error = (TextUtils.isEmpty(reason)
						? message
						: String.format("%s: %s", message.replaceAll("\\.\\S*$", ""), reason));
				return new VolleyError(error, volleyError);
		}
	}

	public abstract static class ResultBaseV1 extends ResultBase {
		@SerializedName("count")
		private int mCount;

		@SerializedName("meta")
		private Meta mMeta;

		public int getCount() {
			return mCount;
		}

		public Meta getMeta() {
			return mMeta;
		}

		/**
		 * A class representing metadata associated with a Citymaps result.
		 */
		public static class Meta {
			/**
			 * The version string.
			 */
			@SerializedName("version")
			private String mVersion;

			/**
			 * The API version string.
			 */
			@SerializedName("api_version")
			private String mApiVersion;

			/**
			 * The amount of time elapsed to make this API call.
			 */
			@SerializedName("elapsed")
			private float mElapsed;

			/**
			 * The entity that made this API call.
			 */
			@SerializedName("caller")
			private Caller mCaller;

			/**
			 * Returns the version string.
			 */
			public String getVersion() {
				return mVersion;
			}

			/**
			 * Returns the API version string.
			 */
			public String getApiVersion() {
				return mApiVersion;
			}

			/**
			 * Returns the amount of time elapsed to make this API call.
			 */
			public float getElapsed() {
				return mElapsed;
			}

			/**
			 * Returns the entity that made this API call.
			 */
			public Caller getCaller() {
				return mCaller;
			}

			/**
			 * A class representing an entity making an API call.
			 */
			public static class Caller {

				/**
				 * The user id of the caller.
				 */
				@SerializedName("user id")
				private String mUserId;

				/**
				 * The name of the caller.
				 */
				@SerializedName("name")
				private String mName;

				/**
				 * The admin level of the caller.
				 */
				@SerializedName("admin level")
				private String mAdminLevel;

				/**
				 * Whether the caller is public.
				 */
				@SerializedName("is public")
				private String mPublic;

				/**
				 * Returns the user id of the caller.
				 */
				public String getUserId() {
					return mUserId;
				}

				/**
				 * Returns the name of the caller.
				 */
				public String getName() {
					return mName;
				}

				/**
				 * Returns the admin level of the caller.
				 */
				public String getAdminLevel() {
					return mAdminLevel;
				}

				/**
				 * Returns whether the caller is public.
				 */
				public boolean isPublic() {
					return Boolean.parseBoolean(mPublic);
				}
			}
		}
	}

	public static class ResultErrorV1 extends ResultBaseV1 {

		@SerializedName("error")
		private String mError;

		@SerializedName("request")
		private String mRequest;

		@SerializedName("response")
		private String mResponse;

		public String getError() {
			return mError;
		}

		public String getRequest() {
			return mRequest;
		}

		public String getResponse() {
			return mResponse;
		}
	}

	public static abstract class ResultSuccessV1<D> extends ResultBaseV1 {
		public abstract D getData();
	}

	public abstract static class ResultBase {

		@SerializedName("code")
		private int mCode;

		@SerializedName("time")
		private long mTime;

		public int getCode() {
			return mCode;
		}

		public long getTime() {
			return mTime;
		}
	}

	public static class ResultError extends ResultBase {

		@SerializedName("message")
		private String mMessage;

		@SerializedName("reason")
		private String mReason;

		public String getMessage() {
			return mMessage;
		}

		public String getReason() {
			return mReason;
		}
	}

	public static abstract class ResultSuccess<D> extends ResultBase {

		@SerializedName("version")
		private String mVersion;

		@SerializedName("build")
		private String mBuild;

		public String getVersion() {
			return mVersion;
		}

		public String getBuild() {
			return mBuild;
		}

		public abstract D getData();
	}

	public static abstract class SearchResultBase {

		@SerializedName("count")
		private int mCount;

		@SerializedName("debug")
		private Object mDebug;

		@SerializedName("meta")
		private Meta mMeta;

		@SerializedName("personalized")
		private boolean mPersonalized;

		@SerializedName("suggestions")
		private Object[] mSuggestions;

		public int getCount() {
			return mCount;
		}

		public Object getDebug() {
			return mDebug;
		}

		public Meta getMeta() {
			return mMeta;
		}

		public boolean isPersonalized() {
			return mPersonalized;
		}

		public Object[] getSuggestions() {
			return mSuggestions;
		}

		public static class Meta {
			@SerializedName("search_type")
			private String mSearchType;

			public String getSearchType() {
				return mSearchType;
			}
		}
	}

	public static abstract class SearchSuccess<D> extends SearchResultBase {
		public abstract D getData();
	}

	protected static class StringArrayTypeAdapterFactory implements TypeAdapterFactory {
		@SuppressWarnings("unchecked")
		@Override
		public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
			if (!String[].class.isAssignableFrom(type.getRawType())) {
				return null;
			}

			final TypeAdapter<String[]> delegate = gson.getDelegateAdapter(this, TypeToken.get(String[].class));

			final TypeAdapter<String[]> adapter = new TypeAdapter<String[]>() {
				@Override
				public void write(JsonWriter out, String[] value) throws IOException {
					delegate.write(out, value);
				}

				@Override
				public String[] read(JsonReader in) throws IOException {
					JsonToken jsonToken = in.peek();
					switch (jsonToken) {
						case STRING:
							String string = in.nextString();
							return string.split(",");
						default:
							return delegate.read(in);
					}
				}
			};

			return (TypeAdapter<T>) adapter;
		}
	}

	/**
	 * A TypeAdapterFactory for serializing and deserializing Deals. Can handle a single Deal object or
	 * an array of Deal objects and always returns the results in an array.
	 */
	protected static class DealsTypeAdapterFactory implements TypeAdapterFactory {
		@SuppressWarnings("unchecked")
		@Override
		public <T> TypeAdapter<T> create(final Gson gson, TypeToken<T> type) {
			if (!Deal[].class.isAssignableFrom(type.getRawType())) {
				return null;
			}

			final TypeAdapter<Deal[]> delegate = gson.getDelegateAdapter(this, TypeToken.get(Deal[].class));

			final TypeAdapter<Deal[]> adapter = new TypeAdapter<Deal[]>() {
				@Override
				public void write(JsonWriter out, Deal[] value) throws IOException {
					if (value == null) {
						out.nullValue();
						return;
					}
					delegate.write(out, value);
				}

				@Override
				public Deal[] read(JsonReader in) throws IOException {
					JsonToken token = in.peek();
					switch (token) {
						case NULL:
							in.nextNull();
							return null;
						case BEGIN_OBJECT:
							Deal deal = gson.fromJson(in, Deal.class);
							return new Deal[]{deal};
						case BEGIN_ARRAY:
						default:
							return delegate.read(in);
					}
				}
			};

			return (TypeAdapter<T>) adapter;
		}
	}
}
