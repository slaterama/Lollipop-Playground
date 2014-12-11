package com.citymaps.mobile.android.view.cards;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.citymaps.mobile.android.R;
import com.citymaps.mobile.android.app.VolleyManager;
import com.citymaps.mobile.android.model.User;
import com.citymaps.mobile.android.util.GraphicsUtils;
import com.citymaps.mobile.android.util.LogEx;

public class UserFixedHeightCardView extends CitymapsCardView<User> {

	protected static final String KEY_AVATAR_IMAGE = "avatarImage";

	public static int getDesiredHeight(Context context, int size) {
		CollectionFixedHeightCardView cardView = new CollectionFixedHeightCardView(context);
		cardView.setBaseSize(size);
		cardView.measure(0, 0);
		return cardView.getMeasuredHeight();
	}

	private ViewGroup mMainContainerView;
	private ImageView mAvatarView;
	private TextView mNameView;
	private TextView mUsernameView;
	private TextView mFollowersView;
	private Button mFollowButton;

	/*
	private CardImageListener.ImageState mMainImageState;
	private CardImageListener.ImageState mAvatarImageState;
	*/

	private UserCardImageListener mUserCardImageListener;
	private UserCardAvatarImageListener mUserCardAvatarImageListener;

	private Animation mAvatarAnimation;

	public UserFixedHeightCardView(Context context) {
		super(context);
	}

	public UserFixedHeightCardView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public UserFixedHeightCardView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	@Override
	public void init(Context context) {
		super.init(context);

		mUserCardImageListener = new UserCardImageListener(context);
		mUserCardAvatarImageListener = new UserCardAvatarImageListener(context);

		mAvatarAnimation = AnimationUtils.loadAnimation(context, R.anim.grow_from_zero);

		View view = View.inflate(context, R.layout.card_user_fixed_height, this);
		mMainContainerView = (ViewGroup) view.findViewById(R.id.card_main_container);
		mImageView = (ImageView) view.findViewById(R.id.card_image);
		mAvatarView = (ImageView) view.findViewById(R.id.card_avatar);
		mNameView = (TextView) view.findViewById(R.id.card_name);
		mUsernameView = (TextView) view.findViewById(R.id.card_username);
		mFollowersView = (TextView) view.findViewById(R.id.card_followers);
		mFollowButton = (Button) view.findViewById(R.id.card_user_action_follow);
	}

	@Override
	public void setBaseSize(int size) {
		super.setBaseSize(size);
		mMainContainerView.getLayoutParams().width = size;
	}

	public ViewGroup getMainContainerView() {
		return mMainContainerView;
	}

	public ImageView getAvatarView() {
		return mAvatarView;
	}

	public ImageView getImageView() {
		return mImageView;
	}

	public TextView getNameView() {
		return mNameView;
	}

	public TextView getFollowersView() {
		return mFollowersView;
	}

	public TextView getUsernameView() {
		return mUsernameView;
	}

	public Button getFollowButton() {
		return mFollowButton;
	}

	/*
	private void checkAvatarImageStatus() {
		// Animate if:
		// mImageView has an image (state is finished)
		// mAvatarView has an image
		// mAvatarView was not "immediate"
		// Otherwise just set visibility to VISIBLE

		if (mMainImageState != CardImageListener.ImageState.FINISHED) {
			return;
		}

		mAvatarView.setVisibility(View.VISIBLE);
		switch (mAvatarImageState) {
			case FINISHED:
				break;
			case LOADED:
				mAvatarView.startAnimation(mAvatarAnimation);
				break;
		}

		LogEx.d(String.format("mMainImageState=%s, mAvatarImageState=%s", mMainImageState, mAvatarImageState));
	}
	*/

	private void checkImages() {
		// mUserCardImageListener.
	}

	@Override
	protected void onBindData(User user) {
		mNameView.setText(user.getName());
		String username = user.getUsername();
		mUsernameView.setText(username);
		int followers = user.getFollowersCount();
		mFollowersView.setText(getResources().getQuantityString(R.plurals.card_followers, followers, followers));
		boolean followed = user.isFollowed();
		mFollowButton.setText(getResources().getString(followed ? R.string.card_unfollow_user : R.string.card_follow_user, username));

		final ImageLoader loader = VolleyManager.getInstance(getContext()).getImageLoader();
		String postcardUrl = user.getPostcardUrl();
		if (TextUtils.isEmpty(postcardUrl)) {
			User.PostcardTemplate postcardTemplate = user.getPostcardTemplate();
			if (postcardTemplate == null) {
				postcardTemplate = User.PostcardTemplate.DEFAULT;
			}
			mImageView.setImageResource(postcardTemplate.getResId());
		} else {
			mImageContainerMap.put(KEY_MAIN_IMAGE, loader.get(postcardUrl,
					mUserCardImageListener.setView(mImageView)));
		}

		String avatarUrl = user.getAvatarUrl();
		if (TextUtils.isEmpty(avatarUrl)) {
			mAvatarView.setImageDrawable(GraphicsUtils.createCircularBitmapDrawable(
					getResources(), R.drawable.default_user_avatar_mini));
		} else {
			mImageContainerMap.put(KEY_AVATAR_IMAGE, loader.get(avatarUrl, mUserCardAvatarImageListener.setView(mAvatarView)));
		}
	}

 	private class UserCardImageListener extends CardImageListener {
		public UserCardImageListener(Context context) {
			super(context);
		}

		@Override
		protected void setImage(Bitmap bitmap, boolean animate) {
			this.mImageView.setImageBitmap(GraphicsUtils.createBlurredBitmap(getContext(), bitmap, 200, 200));
			this.mImageView.setVisibility(View.VISIBLE);
			if (animate && mAnimation != null) {
				startAnimation();
			}
		}
	}

	private class UserCardAvatarImageListener extends CardImageListener {
		public UserCardAvatarImageListener(Context context) {
			super(context);
		}

		@Override
		public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
			super.onResponse(response, isImmediate);
		}

		@Override
		public void setImage(Bitmap bitmap, boolean animate) {
			this.mImageView.setImageDrawable(GraphicsUtils.createCircularBitmapDrawable(
					getResources(), bitmap));
			this.mImageView.setVisibility(View.VISIBLE);
			if (animate && mAnimation != null) {
				startAnimation();
			}
		}

		@Override
		protected Animation onCreateAnimation() {
			return AnimationUtils.loadAnimation(mContext, R.anim.grow_from_zero);
		}
	}
}
