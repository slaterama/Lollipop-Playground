package com.citymaps.mobile.android.model.request;

import android.content.Context;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.citymaps.citymapsengine.LonLat;
import com.citymaps.mobile.android.app.SessionManager;
import com.citymaps.mobile.android.config.Api;
import com.citymaps.mobile.android.config.Endpoint;
import com.citymaps.mobile.android.config.Environment;
import com.citymaps.mobile.android.model.SearchResult;
import com.citymaps.mobile.android.model.SearchResultCollection;
import com.citymaps.mobile.android.model.SearchResultPlace;
import com.citymaps.mobile.android.util.RuntimeTypeAdapterFactory;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import org.joda.time.DateTime;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@SuppressWarnings("SpellCheckingInspection")
public class SearchResultsRequest extends CitymapsGsonRequest<List<SearchResult>> {

	public static final Type TYPE = new TypeToken<List<SearchResult>>(){}.getType();

	public static SearchResultsRequest newFeaturedCollectionsRequest(Context context, LonLat location, int zoom, float radius, int offset, int limit,
															  Response.Listener<List<SearchResult>> listener, Response.ErrorListener errorListener) {
		Environment environment = SessionManager.getInstance(context).getEnvironment();
		String searchId = UUID.randomUUID().toString();
		String urlString = environment.buildUrlString(Endpoint.Type.EXPLORE_FEATURED_COLLECTIONS,
				location.longitude, location.latitude, zoom, radius, searchId, offset, limit);
		return new SearchResultsRequest(Method.GET, urlString, null, null, listener, errorListener);
	}

	public static SearchResultsRequest newFeaturedDealsRequest(Context context, LonLat location, int zoom, float radius, int offset, int limit,
															  Response.Listener<List<SearchResult>> listener, Response.ErrorListener errorListener) {
		Environment environment = SessionManager.getInstance(context).getEnvironment();
		String searchId = UUID.randomUUID().toString();
		String urlString = environment.buildUrlString(Endpoint.Type.EXPLORE_FEATURED_DEALS,
				location.longitude, location.latitude, zoom, radius, searchId, offset, limit);
		return new SearchResultsRequest(Method.GET, urlString, null, null, listener, errorListener);
	}

	public static SearchResultsRequest newFeaturedHeroItemsRequest(Context context, LonLat location, int zoom, float radius, int offset, int limit,
															Response.Listener<List<SearchResult>> listener, Response.ErrorListener errorListener) {
		Environment environment = SessionManager.getInstance(context).getEnvironment();
		String searchId = UUID.randomUUID().toString();
		String urlString = environment.buildUrlString(Endpoint.Type.EXPLORE_FEATURED_HERO_ITEMS,
				location.longitude, location.latitude, zoom, radius, searchId, offset, limit);
		return new SearchResultsRequest(Method.GET, urlString, null, null, listener, errorListener);
	}

	public SearchResultsRequest(Api.Version version, int method, String url,
								Map<String, String> headers, Map<String, String> params,
								Response.Listener<List<SearchResult>> listener, Response.ErrorListener errorListener) {
		super(version, method, url, TYPE, headers, params, listener, errorListener);
	}

	public SearchResultsRequest(int method, String url,
								Map<String, String> headers, Map<String, String> params,
								Response.Listener<List<SearchResult>> listener, Response.ErrorListener errorListener) {
		super(Api.Version.V2, method, url, TYPE, headers, params, listener, errorListener);
	}

	@Override
	protected Response<List<SearchResult>> processParsedNetworkResponse(NetworkResponse response, JsonObject jsonObject) {
		SearchResultWrapper result = getGson().fromJson(jsonObject, SearchResultWrapper.class);
		List<SearchResult> items = result.getData();
		return Response.success(items, HttpHeaderParser.parseCacheHeaders(response));
	}

	@Override
	protected Gson createGson() {
		RuntimeTypeAdapterFactory<SearchResult> searchResultFactory = RuntimeTypeAdapterFactory.of(SearchResult.class, "type")
				.registerSubtype(SearchResultPlace.class, "1")
				.registerSubtype(SearchResultCollection.class, "2");

		return new GsonBuilder()
				.setPrettyPrinting()
				.registerTypeAdapter(DateTime.class, new DateTimeTypeAdapter())
				.registerTypeAdapterFactory(new StringArrayTypeAdapterFactory())
				.registerTypeAdapterFactory(new DealsTypeAdapterFactory())
				.registerTypeAdapterFactory(searchResultFactory)
				.create();
	}

	public static class SearchResultWrapper extends SearchSuccess<List<SearchResult>> {
		@SerializedName("items")
		private List<SearchResult> mItems;

		@Override
		public List<SearchResult> getData() {
			return mItems;
		}
	}
}
