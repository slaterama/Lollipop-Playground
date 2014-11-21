package com.citymaps.mobile.android.preference;

import android.content.Context;
import android.preference.SwitchPreference;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

public class TwoLineSwitchPreference extends SwitchPreference {

	public TwoLineSwitchPreference(Context ctx, AttributeSet attrs, int defStyle) {
		super(ctx, attrs, defStyle);
	}

	public TwoLineSwitchPreference(Context ctx, AttributeSet attrs) {
		super(ctx, attrs);
	}

	public TwoLineSwitchPreference(Context ctx) {
		super(ctx);
	}

	@Override
	protected void onBindView(@NonNull View view) {
		super.onBindView(view);

		TextView textView = (TextView) view.findViewById(android.R.id.title);
		if (textView != null) {
			textView.setSingleLine(false);
		}
	}
}