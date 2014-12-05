//  This singleton implements the "Parametric initialization-on-demand holder idiom" for singletons
//  with immutable parameters in Java (in this case, the application context) as described here:
//  http://unafbapune.blogspot.com/2007/09/parametric-initialization-on-demand.html

package com.citymaps.mobile.android.app;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.LruCache;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class VolleyManager {

	private static Context sContext;

	public static synchronized VolleyManager getInstance(Context context) {
		sContext = context.getApplicationContext();
		return LazyHolder.INSTANCE;
	}

	private static final class LazyHolder {
		private static final VolleyManager INSTANCE = new VolleyManager();
	}

	private RequestQueue mRequestQueue;

	private ImageLoader mImageLoader;

	private VolleyManager() {
		//mRequestQueue = getRequestQueue();
		//mImageLoader = new ImageLoader(mRequestQueue, new CitymapsImageCache());
	}

	public Context getContext() {
		return sContext;
	}

	public RequestQueue getRequestQueue() {
		if (mRequestQueue == null) {
			// sContext is already the application context; no need to convert to
			// application context here
			mRequestQueue = Volley.newRequestQueue(sContext, new CustomHurlStack());
		}
		return mRequestQueue;
	}

	public ImageLoader getImageLoader() {
		if (mImageLoader == null) {
			mImageLoader = new ImageLoader(getRequestQueue(), new CitymapsImageCache());
		}
		return mImageLoader;
	}

	private static class CustomHurlStack extends HurlStack {
		@Override
		protected HttpURLConnection createConnection(URL url) throws IOException {
			HttpURLConnection connection = super.createConnection(url);
			connection.setRequestProperty("Accept-Encoding", "");
			return connection;
		}
	}

	private static class CitymapsImageCache implements ImageLoader.ImageCache {
		private final LruCache<String, Bitmap> mCache;

		public CitymapsImageCache() {
			mCache = new LruCache<String, Bitmap>(20);
		}

		@Override
		public Bitmap getBitmap(String url) {
			return mCache.get(url);
		}

		@Override
		public void putBitmap(String url, Bitmap bitmap) {
			mCache.put(url, bitmap);
		}
	}
}
