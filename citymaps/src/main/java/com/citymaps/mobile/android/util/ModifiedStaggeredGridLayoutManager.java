package com.citymaps.mobile.android.util;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.ViewGroup;

public class ModifiedStaggeredGridLayoutManager extends StaggeredGridLayoutManager {

	protected int mSpanStartPadding;
	protected int mSpanEndPadding;

	public ModifiedStaggeredGridLayoutManager(int spanCount, int orientation) {
		this(spanCount, orientation, 0, 0);
	}

	public ModifiedStaggeredGridLayoutManager(int spanCount, int orientation, int spanStartPadding, int spanEndPadding) {
		super(spanCount, orientation);
		mSpanStartPadding = spanStartPadding;
		mSpanEndPadding = spanEndPadding;
	}

	@Override
	public RecyclerView.LayoutParams generateDefaultLayoutParams() {
		return new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
	}

	@Override
	public RecyclerView.LayoutParams generateLayoutParams(Context c, AttributeSet attrs) {
		return new LayoutParams(c, attrs);
	}

	@Override
	public RecyclerView.LayoutParams generateLayoutParams(ViewGroup.LayoutParams lp) {
		if (lp instanceof ViewGroup.MarginLayoutParams) {
			return new LayoutParams((ViewGroup.MarginLayoutParams) lp);
		} else {
			return new LayoutParams(lp);
		}
	}

	public int getSpanStartPadding() {
		return mSpanStartPadding;
	}

	public void setSpanStartPadding(int spanStartPadding) {
		mSpanStartPadding = spanStartPadding;
	}

	public int getSpanEndPadding() {
		return mSpanEndPadding;
	}

	public void setSpanEndPadding(int spanEndPadding) {
		mSpanEndPadding = spanEndPadding;
	}

	@Override
	public boolean checkLayoutParams(RecyclerView.LayoutParams lp) {
		return lp instanceof LayoutParams;
	}

	public class LayoutParams extends StaggeredGridLayoutManager.LayoutParams {

		protected boolean mFullSpanUsesSpanPadding;

		public LayoutParams(Context c, AttributeSet attrs) {
			super(c, attrs);
		}

		public LayoutParams(int width, int height) {
			super(width, height);
		}

		public LayoutParams(ViewGroup.MarginLayoutParams source) {
			super(source);
		}

		public LayoutParams(ViewGroup.LayoutParams source) {
			super(source);
		}

		public LayoutParams(RecyclerView.LayoutParams source) {
			super(source);
		}

		public boolean isFullSpanUsesSpanPadding() {
			return mFullSpanUsesSpanPadding;
		}

		public void setFullSpanUsesSpanPadding(boolean fullSpanUsesSpanPadding) {
			mFullSpanUsesSpanPadding = fullSpanUsesSpanPadding;
		}
	}
}
