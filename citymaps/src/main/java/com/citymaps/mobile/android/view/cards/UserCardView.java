package com.citymaps.mobile.android.view.cards;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
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
import com.android.volley.toolbox.ImageLoader.ImageContainer;
import com.android.volley.toolbox.Volley;
import com.citymaps.mobile.android.R;
import com.citymaps.mobile.android.app.VolleyManager;
import com.citymaps.mobile.android.model.User;
import com.citymaps.mobile.android.util.GraphicsUtils;

public class UserCardView extends CitymapsCardView<User> {

	public static int getDesiredHeight(Context context, int size) {
		CollectionCardView cardView = new CollectionCardView(context);
		cardView.setBaseSize(size);
		cardView.measure(0, 0);
		return cardView.getMeasuredHeight();
	}

	private ViewGroup mMainContainerView;
	private ImageView mMainImageView;
	private ImageView mAvatarView;
	private TextView mNameView;
	private TextView mUsernameView;
	private TextView mFollowersView;
	private Button mFollowButton;

	private ImageContainer mMainImageContainer;
	private ImageContainer mAvatarImageContainer;

	private CardImageListener mUserCardImageListener;
	private UserCardAvatarImageListener mUserCardAvatarImageListener;

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
	public void init(Context context) {
		super.init(context);

		mUserCardImageListener = new CardImageListener(context);
		mUserCardAvatarImageListener = new UserCardAvatarImageListener(context);

		View view = View.inflate(context, R.layout.card_user, this);
		mMainContainerView = (ViewGroup) view.findViewById(R.id.card_main_container);
		mMainImageView = (ImageView) view.findViewById(R.id.card_image);
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

	public ImageView getMainImageView() {
		return mMainImageView;
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

	@Override
	protected void onBindData(User user) {
		if (mMainImageContainer != null) {
			mMainImageContainer.cancelRequest();
		}

		if (mAvatarImageContainer != null) {
			mAvatarImageContainer.cancelRequest();
		}

		mNameView.setText(user.getName());
		String username = user.getUsername();
		mUsernameView.setText(username);
		int followers = user.getFollowersCount();
		mFollowersView.setText(getResources().getQuantityString(R.plurals.card_followers, followers, followers));
		boolean followed = user.isFollowed();
		mFollowButton.setText(getResources().getString(followed ? R.string.card_unfollow_user : R.string.card_follow_user, username));

		final VolleyManager.CustomImageLoader loader = VolleyManager.getInstance(getContext()).getImageLoader();
		String postcardUrl = user.getPostcardUrl();
		if (TextUtils.isEmpty(postcardUrl)) {
			User.PostcardTemplate postcardTemplate = user.getPostcardTemplate();
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
			mUserCardImageListener.setView(mMainImageView).setImage(postcardBitmap, isImmediate);
		} else {
			mMainImageContainer = loader.get(postcardUrl,
					mUserCardImageListener.setView(mMainImageView), 300, 300, VolleyManager.OPTION_BLUR25);
		}

		String avatarUrl = user.getAvatarUrl();
		if (TextUtils.isEmpty(avatarUrl)) {
			mAvatarView.setImageDrawable(GraphicsUtils.createCircularBitmapDrawable(
					getResources(), R.drawable.default_user_avatar_mini));
		} else {
			int size = getResources().getDimensionPixelSize(R.dimen.avatar_size);
			mAvatarImageContainer = loader.get(avatarUrl,
					mUserCardAvatarImageListener.setView(mAvatarView), size, size, VolleyManager.OPTION_CIRCLE);
		}
	}

	private class UserCardAvatarImageListener extends CardImageListener {
		public UserCardAvatarImageListener(Context context) {
			super(context);
		}

		@Override
		protected Animation onCreateAnimation() {
			return AnimationUtils.loadAnimation(mContext, R.anim.grow_from_zero);
		}
	}
}
