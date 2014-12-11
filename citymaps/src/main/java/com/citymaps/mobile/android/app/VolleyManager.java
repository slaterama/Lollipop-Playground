//  This singleton implements the "Parametric initialization-on-demand holder idiom" for singletons
//  with immutable parameters in Java (in this case, the application context) as described here:
//  http://unafbapune.blogspot.com/2007/09/parametric-initialization-on-demand.html

package com.citymaps.mobile.android.app;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.LruCache;
import com.android.volley.Network;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.*;
import com.citymaps.mobile.android.util.LogEx;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.OkUrlFactory;

import javax.net.ssl.SSLContext;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class VolleyManager {

	// Default maximum disk usage in bytes
	private static final int DEFAULT_DISK_USAGE_BYTES = 25 * 1024 * 1024;

	// Default cache folder name
	private static final String DEFAULT_CACHE_DIR = "photos";

	private static Context sContext;

	static {
		HttpURLConnection.setFollowRedirects(true);
	}

	public static synchronized VolleyManager getInstance(Context context) {
		sContext = context.getApplicationContext();
		return LazyHolder.INSTANCE;
	}

	private static final class LazyHolder {
		private static final VolleyManager INSTANCE = new VolleyManager();
	}

	// Most code copied from "Volley.newRequestQueue(..)", we only changed cache directory
	private static RequestQueue newRequestQueue(Context context) {
		// define cache folder
		File rootCache = context.getExternalCacheDir();
		if (rootCache == null) {
			LogEx.w("Can't find External Cache Dir, "
					+ "switching to application specific cache directory");
			rootCache = context.getCacheDir();
		}

		File cacheDir = new File(rootCache, DEFAULT_CACHE_DIR);
		boolean success = (cacheDir.mkdirs() || cacheDir.isDirectory());

		HttpStack stack = new CustomHurlStack();
		Network network = new BasicNetwork(stack);
		DiskBasedCache diskBasedCache = new DiskBasedCache(cacheDir, DEFAULT_DISK_USAGE_BYTES);
		RequestQueue queue = new RequestQueue(diskBasedCache, network);
		queue.start();

		return queue;
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
			mRequestQueue = newRequestQueue(sContext); //Volley.newRequestQueue(sContext, new CustomHurlStack());
		}
		return mRequestQueue;
	}

	public ImageLoader getImageLoader() {
		if (mImageLoader == null) {
			mImageLoader = new ImageLoader(getRequestQueue(), new BitmapLruCache()); //new CitymapsImageCache());
		}
		return mImageLoader;
	}

	private static class CustomHurlStack extends HurlStack {
		private final OkUrlFactory mFactory;

		public CustomHurlStack() {
			this(new OkHttpClient());
		}

		public CustomHurlStack(OkHttpClient client) {
			if (client == null) {
				throw new NullPointerException("Client may not be null");
			}

			// As per https://gist.github.com/JakeWharton/5616899
			// and https://github.com/square/okhttp/issues/184
			try {
				SSLContext sslContext = SSLContext.getInstance("TLS");
				sslContext.init(null, null, null);
				client.setSslSocketFactory(sslContext.getSocketFactory());
			} catch (Exception e) {
				throw new AssertionError(); // The system has no TLS. Just give up.
			}

			mFactory = new OkUrlFactory(client);
		}

		@Override
		protected HttpURLConnection createConnection(URL url) throws IOException {
			HttpURLConnection connection = mFactory.open(url);
			connection.setRequestProperty("Accept-Encoding", "");
			connection.setInstanceFollowRedirects(true);
			return connection;
		}
	}

	private static class BitmapLruCache extends LruCache<String, Bitmap>
			implements ImageLoader.ImageCache {

		public BitmapLruCache() {
			this(getDefaultLruCacheSize());
		}

		public BitmapLruCache(int sizeInKiloBytes) {
			super(sizeInKiloBytes);
		}

		@Override
		protected int sizeOf(String key, Bitmap value) {
			return value.getRowBytes() * value.getHeight() / 1024;
		}

		@Override
		public Bitmap getBitmap(String url) {
			return get(url);
		}

		@Override
		public void putBitmap(String url, Bitmap bitmap) {
			put(url, bitmap);
		}

		public static int getDefaultLruCacheSize() {
			final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
			return maxMemory / 8;
		}
	}
}
