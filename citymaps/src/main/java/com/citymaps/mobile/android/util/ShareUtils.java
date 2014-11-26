package com.citymaps.mobile.android.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Parcelable;
import android.text.Html;
import com.citymaps.mobile.android.R;

import java.util.*;

public class ShareUtils {

	private static final String TEXT_INTENT_TYPE = "text/plain";
	private static final String HTML_INTENT_TYPE = "text/html";
	private static final String EMAIL_INTENT_TYPE = "message/rfc822";
	
	public static void shareApp(Context context) {
		final PackageManager packageManager = context.getPackageManager();

		Intent emailIntent = new Intent(Intent.ACTION_SEND);
		emailIntent.setType(EMAIL_INTENT_TYPE);
		emailIntent.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.pref_share_app_email_subject));
		emailIntent.putExtra(Intent.EXTRA_TEXT, context.getString(R.string.pref_share_app_email_text));

		Intent htmlIntent = new Intent(Intent.ACTION_SEND);
		htmlIntent.setType(HTML_INTENT_TYPE);
		htmlIntent.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.pref_share_app_email_subject));
		htmlIntent.putExtra(Intent.EXTRA_TEXT, Html.fromHtml(context.getString(R.string.pref_share_app_email_text)));

		Intent textIntent = new Intent(Intent.ACTION_SEND);
		textIntent.setType(TEXT_INTENT_TYPE);
		textIntent.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.pref_share_app_email_subject));
		textIntent.putExtra(Intent.EXTRA_TEXT, context.getString(R.string.pref_share_app_email_text));

		Comparator<ResolveInfo> comparator = new Comparator<ResolveInfo>() {
			@Override
			public int compare(ResolveInfo lhs, ResolveInfo rhs) {
				String lhsPackageName = lhs.activityInfo.packageName;
				String rhsPackageName = rhs.activityInfo.packageName;
				CommonPackageInfo lhsPackageInfo = CommonPackageInfo.fromPackageName(lhsPackageName);
				CommonPackageInfo rhsPackageInfo = CommonPackageInfo.fromPackageName(rhsPackageName);
				int lhsSortCategory = (lhs.isDefault ? Integer.MIN_VALUE : lhsPackageInfo.getSortCategory());
				int rhsSortCategory = (rhs.isDefault ? Integer.MIN_VALUE : rhsPackageInfo.getSortCategory());
				if (lhsSortCategory < rhsSortCategory) {
					return -1;
				} else if (lhsSortCategory > rhsSortCategory) {
					return 1;
				} else {
					String lhsLabel = String.valueOf(packageManager.getApplicationLabel(lhs.activityInfo.applicationInfo));
					String rhsLabel = String.valueOf(packageManager.getApplicationLabel(rhs.activityInfo.applicationInfo));
					return lhsLabel.compareToIgnoreCase(rhsLabel);
				}
			}
		};

		Map<ResolveInfo, Intent> intentMap = new TreeMap<ResolveInfo, Intent>(comparator);
		Set<String> packageNameSet = new HashSet<String>();
		int flags = 0;
		List<ResolveInfo> htmlInfos = packageManager.queryIntentActivities(htmlIntent, flags);
		for (ResolveInfo info : htmlInfos) {
			Intent intent = new Intent(Intent.ACTION_SEND);
			intent.setPackage(info.activityInfo.packageName);
			intent.setType(HTML_INTENT_TYPE);
			intent.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.pref_share_app_email_subject));
			intent.putExtra(Intent.EXTRA_TEXT, Html.fromHtml(context.getString(R.string.pref_share_app_email_html)));
			String packageName = info.activityInfo.packageName;
			if (!packageNameSet.contains(packageName)) {
				packageNameSet.add(packageName);
				intentMap.put(info, intent);
			}
		}

		List<ResolveInfo> emailInfos = packageManager.queryIntentActivities(emailIntent, flags);
		for (ResolveInfo info : emailInfos) {
			Intent intent = new Intent(Intent.ACTION_SEND);
			intent.setPackage(info.activityInfo.packageName);
			intent.setType(EMAIL_INTENT_TYPE);
			intent.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.pref_share_app_email_subject));
			intent.putExtra(Intent.EXTRA_TEXT, context.getString(R.string.pref_share_app_email_text));
			String packageName = info.activityInfo.packageName;
			if (!packageNameSet.contains(packageName)) {
				packageNameSet.add(packageName);
				intentMap.put(info, intent);
			}
		}

		List<ResolveInfo> textInfos = packageManager.queryIntentActivities(textIntent, flags);
		for (ResolveInfo info : textInfos) {
			Intent intent = new Intent(Intent.ACTION_SEND);
			intent.setPackage(info.activityInfo.packageName);
			intent.setType(TEXT_INTENT_TYPE);
			intent.putExtra(Intent.EXTRA_TEXT, context.getString(R.string.pref_share_app_text));
			String packageName = info.activityInfo.packageName;
			if (!packageNameSet.contains(packageName)) {
				packageNameSet.add(packageName);
				intentMap.put(info, intent);
			}
		}

		if (intentMap.size() > 0) {
			List<Intent> intents = new ArrayList<Intent>(intentMap.values());
			Intent lastIntent = intents.remove(intents.size() - 1);
			Intent chooserIntent = Intent.createChooser(lastIntent, context.getString(R.string.pref_share_app_intent_title));
			chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intents.toArray(new Parcelable[intents.size()]));
			context.startActivity(chooserIntent);
		}
	}
	
	private ShareUtils() {
	}
}
