package com.citymaps.mobile.android.view.cards.explore;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import com.citymaps.mobile.android.R;

public class BestAroundCollectionViewHolder extends RecyclerView.ViewHolder {

	private TextView mNameView;

	public BestAroundCollectionViewHolder(View itemView) {
		super(itemView);
		mNameView = (TextView) itemView.findViewById(R.id.explore_card_best_around_name);
	}

	public TextView getNameView() {
		return mNameView;
	}
}
