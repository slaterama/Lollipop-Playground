package com.citymaps.mobile.android.view.explorenew;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;
import com.citymaps.mobile.android.R;

public class ViewAllCardView extends CitymapsCardView<Integer> {

	private TextView mLabelView;

	public ViewAllCardView(Context context) {
		super(context);
	}

	public ViewAllCardView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ViewAllCardView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	@Override
	protected void init(Context context) {
		View.inflate(context, R.layout.card_view_all, this);
		setCardBackgroundColor(getResources().getColor(R.color.color_primary));
		mLabelView = (TextView) findViewById(R.id.card_view_all_label);
		super.init(context);
	}

	@Override
	public void setDefaultCardSize(int size) {

	}

	@Override
	public void onBindView(Integer data) {
		mLabelView.setText(data);
	}
}
