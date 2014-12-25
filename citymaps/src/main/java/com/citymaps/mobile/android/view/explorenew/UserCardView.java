package com.citymaps.mobile.android.view.explorenew;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.volley.toolbox.ImageLoader;
import com.citymaps.mobile.android.R;
import com.citymaps.mobile.android.app.VolleyManager;
import com.citymaps.mobile.android.model.User;
import com.citymaps.mobile.android.util.GraphicsUtils;

public class UserCardView extends CitymapsCardView<User> {

	public static int getDesiredHeight(Context context, int defaultCardSize) {
		UserCardView cardView = new UserCardView(context);
		cardView.setDefaultCardSize(defaultCardSize);
		cardView.measure(0, 0);
		return cardView.getMeasuredHeight();
	}

	private ViewGroup mMainContainerView;
	private ViewGroup mInfoContainerView;
	private ImageView mMainImageView;
	private ImageView mAvatarView;
	private TextView mNameView;
	private TextView mUsernameView;
	private TextView mFollowersView;
	private Button mFollowButton;

	public UserCardView(Context context) {
		super(context);
	}

	public UserCardView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public UserCardView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	@Override
	protected void init(Context context) {
		View.inflate(context, R.layout.card_user_new, this);
		mMainContainerView = (ViewGroup) findViewById(R.id.card_main_container);
		mInfoContainerView = (ViewGroup) findViewById(R.id.card_info_container);
		mMainImageView = (ImageView) findViewById(R.id.card_image);
		mAvatarView = (ImageView) findViewById(R.id.card_avatar);
		mNameView = (TextView) findViewById(R.id.card_name);
		mUsernameView = (TextView) findViewById(R.id.card_username);
		mFollowersView = (TextView) findViewById(R.id.card_followers);
		mFollowButton = (Button) findViewById(R.id.card_user_action_follow);
		super.init(context);
	}

	@Override
	public void setDefaultCardSize(int defaultCardSize) {
		mMainContainerView.getLayoutParams().width = defaultCardSize;
		mMainContainerView.requestLayout();
	}

	@Override
	public void onBindView(User data, boolean inInitialLayout) {
		mNameView.setText(data.getName());
		String username = data.getUsername();
		mUsernameView.setText(username);
		int followers = data.getFollowersCount();
		mFollowersView.setText(getResources().getQuantityString(R.plurals.card_followers, followers, followers));
		boolean followed = data.isFollowed();
		mFollowButton.setText(getResources().getString(followed ? R.string.card_unfollow_user : R.string.card_follow_user, username));

		String postcardUrl = data.getPostcardUrl();
		if (TextUtils.isEmpty(postcardUrl)) {
			User.PostcardTemplate postcardTemplate = data.getPostcardTemplate();
			if (postcardTemplate == null) {
				postcardTemplate = User.PostcardTemplate.DEFAULT;
			}
			ImageLoader.ImageCache cache = VolleyManager.getInstance(getContext()).getImageCache();
			String cacheKey = "#" + VolleyManager.OPTION_BLUR25 + postcardTemplate.toString();
			Bitmap postcardBitmap = cache.getBitmap(cacheKey);
			boolean isImmediate = (postcardBitmap != null);
			if (!isImmediate) {
				BitmapDrawable postcardDrawable = (BitmapDrawable) getResources().getDrawable(postcardTemplate.getResId());
				postcardBitmap = VolleyManager.BitmapEditor.newEditor(getContext(), VolleyManager.OPTION_BLUR25).edit(postcardDrawable.getBitmap());
				cache.putBitmap(cacheKey, postcardBitmap);
			}
			new ImageListener(getContext(), mMainImageView).setBitmap(postcardBitmap, isImmediate);
		} else {
			mImageContainers.add(mImageLoader.get(postcardUrl,
					new ImageListener(getContext(), mMainImageView), 300, 300, VolleyManager.OPTION_BLUR25));
		}

		String avatarUrl = data.getAvatarUrl();
		if (TextUtils.isEmpty(avatarUrl)) {
			mAvatarView.setImageDrawable(GraphicsUtils.createCircularBitmapDrawable(
					getResources(), R.drawable.default_user_avatar_mini));
		} else {
			int size = getResources().getDimensionPixelSize(R.dimen.avatar_size);
			mImageContainers.add(mImageLoader.get(avatarUrl,
					new AvatarImageListener(getContext(), mAvatarView), size, size, VolleyManager.OPTION_CIRCLE));
		}
	}

	@Override
	protected void resetView() {
		super.resetView();
		mMainImageView.setImageDrawable(null);
		mAvatarView.setImageDrawable(null);
	}

	protected class ImageListener extends AnimatingImageListener {
		public ImageListener(Context context, ImageView imageView) {
			super(context, imageView);
		}

		/*
		@Override
		public void onImageLoadComplete() {
			mPendingImageViews.remove(getImageView());
			if (mPendingImageViews.size() == 0) {
				notifyBindComplete();
			}
		}
		*/
	}

	protected class AvatarImageListener extends AnimatingImageListener {
		public AvatarImageListener(Context context, ImageView imageView) {
			super(context, imageView);
			setAnimationResId(R.anim.grow_from_zero);
		}

		/*
		@Override
		public void onImageLoadComplete() {
			mPendingImageViews.remove(getImageView());
			if (mPendingImageViews.size() == 0) {
				notifyBindComplete();
			}
		}
		*/
	}
}
