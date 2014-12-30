//  This singleton implements the "Parametric initialization-on-demand holder idiom" for singletons
//  with immutable parameters in Java (in this case, the application context) as described here:
//  http://unafbapune.blogspot.com/2007/09/parametric-initialization-on-demand.html

//  Note: Much of the logic/decisions here come from the following blog:
//  http://blog.lemberg.co.uk/volley-part-1-quickstart
//  http://blog.lemberg.co.uk/volley-part-2-application-model
//  http://blog.lemberg.co.uk/volley-part-3-image-loader

package com.citymaps.mobile.android.app;

import android.content.Context;
import android.graphics.*;
import android.media.ThumbnailUtils;
import android.os.Build;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.Element;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.ScriptIntrinsicBlur;
import android.text.TextUtils;
import android.util.LruCache;
import com.android.volley.Network;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
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

	public static final String OPTION_BLUR25 = "blur25";
	public static final String OPTION_CIRCLE = "circle";
	public static final String OPTION_ROUNDED_SQUARE = "roundedSquare";

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

	private CustomImageLoader mImageLoader;

	private ImageLoader.ImageCache mImageCache;

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

	public CustomImageLoader getImageLoader() {
		if (mImageLoader == null) {
			mImageLoader = new CustomImageLoader(getRequestQueue(), getImageCache());
		}
		return mImageLoader;
	}

	public ImageLoader.ImageCache getImageCache() {
		if (mImageCache == null) {
			mImageCache = new BitmapLruCache();
		}
		return mImageCache;
	}

	public static class CustomImageLoader extends ImageLoader {
		private ImageCache mImageCache;

		public CustomImageLoader(RequestQueue queue, ImageCache imageCache) {
			super(queue, imageCache);
			mImageCache = imageCache;
		}

		public ImageContainer get(String requestUrl, ImageListener listener, String... options) {
			return get(requestUrl, listener, 0, 0, options);
		}

		public ImageContainer get(final String requestUrl, final ImageListener imageListener, int maxWidth, int maxHeight, final String... options) {
			final String cacheKey = getCacheKey(requestUrl, maxWidth, maxHeight, options);
			Bitmap cachedBitmap = mImageCache.getBitmap(cacheKey);
			if (cachedBitmap != null) {
				// Return the cached bitmap.
				ImageContainer container = new ImageContainer(cachedBitmap, requestUrl, null, null);
				imageListener.onResponse(container, true);
				return container;
			}

			ImageContainer container = super.get(requestUrl, new ImageListener() {
				@Override
				public void onResponse(ImageContainer response, boolean isImmediate) {
					Bitmap bitmap = response.getBitmap();
					if (bitmap != null && options != null) {
						// Do stuff to bitmap
						for (String option : options) {
							bitmap = BitmapEditor.newEditor(sContext, option).edit(bitmap);
						}
						ImageContainer container = new ImageContainer(bitmap, requestUrl, null, null); // TODO ???
						mImageCache.putBitmap(cacheKey, bitmap);
						imageListener.onResponse(container, false);
					} else {
						imageListener.onResponse(response, isImmediate);
					}
				}

				@Override
				public void onErrorResponse(VolleyError error) {
					imageListener.onErrorResponse(error);
				}
			}, maxWidth, maxHeight);

			return container;
		}

		private static String getCacheKey(String url, int maxWidth, int maxHeight, String... options) {
			final String prefix;
			final String optionsString;
			if (options != null && options.length > 0) {
				prefix = "#";
				optionsString = TextUtils.join("#", options);
			} else {
				prefix = "";
				optionsString = "";
			}
			return new StringBuilder(url.length() + prefix.length() + optionsString.length() + 12).append("#W").append(maxWidth)
					.append("#H").append(maxHeight).append(prefix).append(optionsString).append(url).toString();
		}
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

	public static abstract class BitmapEditor {
		public static BitmapEditor newEditor(Context context, String option) {
			if (TextUtils.equals(option, OPTION_BLUR25) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) { // TODO Blur for < LOLLIPOP
				return new Blur25BitmapEditor(context);
			} else if (TextUtils.equals(option, OPTION_CIRCLE)) {
				return new CircleBitmapEditor(context);
			} else if (TextUtils.equals(option, OPTION_ROUNDED_SQUARE)) {
				return new RoundedSquareBitmapEditor(context);
			} else {
				return new DefaultBitmapEditor(context);
			}
		}

		protected Context mContext;

		public BitmapEditor(Context context) {
			mContext = context;
		}

		public Bitmap edit(Bitmap in) {
			return in;
		}
	}

	private static class DefaultBitmapEditor extends BitmapEditor {
		public DefaultBitmapEditor(Context context) {
			super(context);
		}
	}

	private static class Blur25BitmapEditor extends BitmapEditor {
		public Blur25BitmapEditor(Context context) {
			super(context);
		}

		@Override
		public Bitmap edit(Bitmap in) {
			//Bitmap thumb = ThumbnailUtils.extractThumbnail(in, width, height);
			Bitmap out = Bitmap.createBitmap(in.getWidth(), in.getHeight(), Bitmap.Config.ARGB_8888);
			RenderScript rs = RenderScript.create(sContext.getApplicationContext());
			ScriptIntrinsicBlur blurScript = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
			Allocation allocationIn = Allocation.createFromBitmap(rs, in);
			Allocation allocationOut = Allocation.createFromBitmap(rs, out);
			blurScript.setRadius(25.0f);
			blurScript.setInput(allocationIn);
			blurScript.forEach(allocationOut);
			allocationOut.copyTo(out);
			in.recycle();
//			rs.destroy();
			return out;
		}
	}

	private static class CircleBitmapEditor extends BitmapEditor {
		public CircleBitmapEditor(Context context) {
			super(context);
		}

		@Override
		public Bitmap edit(Bitmap in) {
			int width = in.getWidth();
			int height = in.getHeight();
			int size = Math.min(width, height);
			if (width != height) {
				in = ThumbnailUtils.extractThumbnail(in, size, size);
			}

			Bitmap output = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
			Canvas canvas = new Canvas(output);

			final int color = Color.BLACK; //0xff424242;
			final Paint paint = new Paint();
			final Rect rect = new Rect(0, 0, size, size);
			final RectF rectF = new RectF(rect);
			final float radius = size / 2.0f;

			paint.setAntiAlias(true);
			canvas.drawARGB(0, 0, 0, 0);
			paint.setColor(color);
			canvas.drawRoundRect(rectF, radius, radius, paint);

			paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
			canvas.drawBitmap(in, rect, rect, paint);

			return output;
		}
	}

	private static class RoundedSquareBitmapEditor extends BitmapEditor {
		public RoundedSquareBitmapEditor(Context context) {
			super(context);
		}
	}
}
