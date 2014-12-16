package com.citymaps.mobile.android.widget;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

public class RecyclerViewEx extends RecyclerView {

	private OnSizeChangedListener mOnSizeChangedListener;

	public RecyclerViewEx(Context context) {
		super(context);
	}

	public RecyclerViewEx(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public RecyclerViewEx(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public void setOnSizeChangedListener(OnSizeChangedListener onSizeChangedListener) {
		mOnSizeChangedListener = onSizeChangedListener;
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		if (mOnSizeChangedListener != null) {
			mOnSizeChangedListener.onSizeChanged(this, w, h, oldw, oldh);
		}
	}
}
