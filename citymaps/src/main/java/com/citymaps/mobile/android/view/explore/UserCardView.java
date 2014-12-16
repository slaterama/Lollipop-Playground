package com.citymaps.mobile.android.view.explore;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.citymaps.mobile.android.R;
import com.citymaps.mobile.android.model.User;

public class UserCardView extends CitymapsCardView {

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

	private User mData;

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
	protected void inflateView(Context context) {
		View.inflate(context, R.layout.card_collection_new, this);
	}

	@Override
	protected void init(Context context) {
		super.init(context);
		mMainContainerView = (ViewGroup) findViewById(R.id.card_main_container);
		mInfoContainerView = (ViewGroup) findViewById(R.id.card_info_container);
		mMainImageView = (ImageView) findViewById(R.id.card_image);
		mAvatarView = (ImageView) findViewById(R.id.card_avatar);
		mNameView = (TextView) findViewById(R.id.card_name);
		mUsernameView = (TextView) findViewById(R.id.card_username);
		mFollowersView = (TextView) findViewById(R.id.card_followers);
		mFollowButton = (Button) findViewById(R.id.card_user_action_follow);
	}

	@Override
	public void setDefaultCardSize(int defaultCardSize) {
		mMainContainerView.getLayoutParams().width = defaultCardSize;
		mMainContainerView.requestLayout();
	}

	public void bindData(User data) {
		mData = data;
		mNameView.setText(data.getName());
		String username = data.getUsername();
		mUsernameView.setText(username);
		int followers = data.getFollowersCount();
		mFollowersView.setText(getResources().getQuantityString(R.plurals.card_followers, followers, followers));
		boolean followed = data.isFollowed();
		mFollowButton.setText(getResources().getString(followed ? R.string.card_unfollow_user : R.string.card_follow_user, username));

		// TODO Image stuff
	}
}
