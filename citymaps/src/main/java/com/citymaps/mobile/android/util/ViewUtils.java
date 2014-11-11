package com.citymaps.mobile.android.util;

import android.support.annotation.NonNull;
import android.text.Spannable;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.text.style.UnderlineSpan;
import android.widget.TextView;

public class ViewUtils {

	public static void setSpannableText(TextView textView, Spannable spannable) {
		textView.setMovementMethod(new LinkMovementMethod());

		for (URLSpan u: spannable.getSpans(0, spannable.length(), URLSpan.class)) {
			spannable.setSpan(new UnderlineSpan() {
				public void updateDrawState(@NonNull TextPaint tp) {
					tp.setUnderlineText(false);
				}
			}, spannable.getSpanStart(u), spannable.getSpanEnd(u), 0);
		}
		textView.setText(spannable);
	}

	private ViewUtils() {
	}
}
