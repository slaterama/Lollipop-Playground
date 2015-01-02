package com.citymaps.mobile.android.util;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import com.citymaps.mobile.android.R;
import com.citymaps.mobile.android.view.cards.*;
import com.citymaps.mobile.android.widget.OnSizeChangedListener;

public class CardSizeHelper
		implements OnSizeChangedListener {
	private Context mContext;
	private boolean mConstrainRecyclerHeight;

	private boolean mUseCompatPadding;
	private int mCardMaxElevation;
	private int mCardPerceivedMargin;

	private float mDefaultCardsAcross;
	private float mHeroCardsAcross;
	private float mFeaturedCollectionsCardsAcross;
	private float mFeaturedMappersCardsAcross;
	private float mFeaturedDealsCardsAcross;

	private int mHeroDefaultCardSize;
	private Rect mHeroCardRect;
	private Rect mFeaturedCollectionsCardRect;
	private Rect mFeaturedMappersCardRect;
	private Rect mFeaturedDealsCardRect;

	public CardSizeHelper(Context context, boolean constrainRecyclerHeight) {
		mContext = context;
		mConstrainRecyclerHeight = constrainRecyclerHeight;

		// Get dimensions and other resources for recycler view card layout

		Resources resources = context.getResources();
		mUseCompatPadding = resources.getBoolean(R.bool.explore_card_use_compat_padding);
		mCardMaxElevation = resources.getDimensionPixelOffset(R.dimen.explore_card_max_elevation);
		mCardPerceivedMargin = resources.getDimensionPixelOffset(R.dimen.explore_card_inner_margin);
		mDefaultCardsAcross = ResourcesUtils.getFloat(resources, R.dimen.explore_default_cards_across, 2.0f);
		mHeroCardsAcross = ResourcesUtils.getFloat(resources, R.dimen.explore_hero_cards_across, 1.0f);
		mFeaturedCollectionsCardsAcross = ResourcesUtils.getFloat(resources, R.dimen.explore_featured_collections_cards_across, 2.0f);
		mFeaturedMappersCardsAcross = ResourcesUtils.getFloat(resources, R.dimen.explore_featured_mappers_cards_across, 2.0f);
		mFeaturedDealsCardsAcross = ResourcesUtils.getFloat(resources, R.dimen.explore_featured_deals_cards_across, 2.0f);
	}

	public int getCardPerceivedMargin() {
		return mCardPerceivedMargin;
	}

	private int getCardSize(View view, float cardsAcross) {
			/*
			 * NOTE: This returns the "perceived" card width. That is, the width of the card minus any shadow/elevation element.
			 * When setting the actual width of cards (i.e. in the createViewHolder method of the various adapters),
			 * any "compat" padding will need to be added if appropriate.
			 */

		int elevationFactor = (mUseCompatPadding ? mCardMaxElevation : 0);
		int perceivedPaddingLeft = view.getPaddingLeft() + elevationFactor;
		int perceivedPaddingRight = view.getPaddingRight() + elevationFactor;
		int marginCount = (int) (Math.ceil(cardsAcross)) - 1;
		int perceivedTotalMargin = mCardPerceivedMargin * marginCount;
		int perceivedAvailableWidth = view.getWidth() - perceivedPaddingLeft - perceivedPaddingRight - perceivedTotalMargin;
		return Math.max((int) (perceivedAvailableWidth / cardsAcross), 0);
	}

	public void updateCardLayoutParams(ExploreCardView cardView) {
		Rect rect;
		CardType type = (CardType) cardView.getTag();
		if (type == null) {
			return;
		}
		switch (type) {
			case HERO:
				rect = mHeroCardRect;
				cardView.setDefaultCardSize(mHeroDefaultCardSize);
				break;
			case FEATURED_COLLECTIONS:
				rect = mFeaturedCollectionsCardRect;
				cardView.setDefaultCardSize(rect.width());
				break;
			case FEATURED_MAPPERS:
				rect = mFeaturedMappersCardRect;
				cardView.setDefaultCardSize(rect.width());
				break;
			case FEATURED_DEALS:
				rect = mFeaturedDealsCardRect;
				cardView.setDefaultCardSize(rect.width());
				break;
			default:
				return;
		}
		int actualCardWidth = rect.width() + (mUseCompatPadding ? 2 * mCardMaxElevation : 0);
		cardView.setLayoutParams(new RecyclerView.LayoutParams(actualCardWidth, rect.height()));
	}

	@Override
	public void onSizeChanged(View v, int w, int h, int oldw, int oldh) {
		if (w != oldw) {
			final Rect cardRect;
			CardType type = (CardType) v.getTag();
			switch (type) {
				case HERO: {
					mHeroDefaultCardSize = getCardSize(v, mDefaultCardsAcross); // Card size across for "default"
					int cardSize = getCardSize(v, mHeroCardsAcross);
					mHeroCardRect = new Rect(0, 0, cardSize,
							HeroCardView.getDesiredHeight(mContext, mHeroDefaultCardSize));
					cardRect = mHeroCardRect;
					break;
				}
				case FEATURED_COLLECTIONS: {
					int cardSize = getCardSize(v, mFeaturedCollectionsCardsAcross);
					mFeaturedCollectionsCardRect = new Rect(0, 0, cardSize,
							CollectionCardView.getDesiredHeight(mContext, cardSize));
					cardRect = mFeaturedCollectionsCardRect;
					break;
				}
				case FEATURED_MAPPERS: {
					int cardSize = getCardSize(v, mFeaturedMappersCardsAcross);
					mFeaturedMappersCardRect = new Rect(0, 0, cardSize,
							UserCardView.getDesiredHeight(mContext, cardSize));
					cardRect = mFeaturedMappersCardRect;
					break;
				}
				case FEATURED_DEALS: {
					int cardSize = getCardSize(v, mFeaturedDealsCardsAcross);
					mFeaturedDealsCardRect = new Rect(0, 0, cardSize,
							DealCardView.getDesiredHeight(mContext, cardSize));
					cardRect = mFeaturedDealsCardRect;
					break;
				}
				default:
					return;
			}
			if (mConstrainRecyclerHeight) {
				v.getLayoutParams().height = v.getPaddingTop() + cardRect.height() + v.getPaddingBottom();
				v.requestLayout();
			}
		}
	}
}
