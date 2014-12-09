package com.citymaps.mobile.android.view.cards;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.citymaps.mobile.android.R;
import com.citymaps.mobile.android.app.VolleyManager;
import com.citymaps.mobile.android.model.User;
import com.citymaps.mobile.android.util.DrawableUtils;

public class UserFixedHeightCardView extends CitymapsCardView<User> {

	public static int getDesiredHeight(Context context, int size) {
		CollectionFixedHeightCardView cardView = new CollectionFixedHeightCardView(context);
		cardView.setBaseSize(size);
		cardView.measure(0, 0);
		return cardView.getMeasuredHeight();
	}

	private ViewGroup mMainContainerView;
	private ImageView mImageView;
	private ImageView mAvatarView;
	private TextView mNameView;
	private TextView mUsernameView;
	private TextView mFollowersView;
	private Button mFollowButton;

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

	@Override
	protected void onBindData(User user) {
		mImageView.setImageResource(R.drawable.forrest_point);
		mNameView.setText(user.getName());
		String username = user.getUsername();
		mUsernameView.setText(username);
		int followers = user.getFollowersCount();
		mFollowersView.setText(getResources().getQuantityString(R.plurals.card_followers, followers, followers));
		boolean followed = user.isFollowed();
		mFollowButton.setText(getResources().getString(followed ? R.string.card_unfollow_user : R.string.card_follow_user, username));

		String avatarUrl = user.getAvatarUrl();
		if (TextUtils.isEmpty(avatarUrl)) {
			mAvatarView.setImageDrawable(DrawableUtils.createCircularBitmapDrawable(
					getResources(), R.drawable.default_user_avatar_mini));
		} else {
			final ImageLoader loader = VolleyManager.getInstance(getContext()).getImageLoader();
			loader.get(avatarUrl,
					new ImageLoader.ImageListener() {
						@Override
						public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
							Bitmap bitmap = response.getBitmap();
							if (bitmap == null) {
								mAvatarView.setImageDrawable(null);
							} else {
								mAvatarView.setImageDrawable(DrawableUtils.createCircularBitmapDrawable(
										getResources(), bitmap));
							}
						}

						@Override
						public void onErrorResponse(VolleyError error) {
							mAvatarView.setImageDrawable(getMiniAvatarNoImageDrawable(getResources()));
						}
					});
		}
	}
}
