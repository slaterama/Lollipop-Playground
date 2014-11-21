package com.citymaps.mobile.android.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.widget.Toast;
import com.citymaps.mobile.android.R;

public class CommonUtils {

	public static boolean notifyIfNoNetwork(Context context) {
		ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (manager.getActiveNetworkInfo() == null) {
			Toast.makeText(context, R.string.error_message_no_connection, Toast.LENGTH_SHORT).show();
			return true;
		}
		return false;
	}

	private CommonUtils() {
	}
}
