package com.citymaps.mobile.android.model;

import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.Observable;

/**
 * Additional settings associated with a user of the Citymaps application.
 */
public class UserSettings extends Observable {

	@SerializedName("settings_id")
	private String mId;

	@SerializedName("user_id")
	private String mUserId;

	@SerializedName("email_notifications")
	private int mEmailNotifications;

	@SerializedName("created_at")
	private Date mCreatedAt;

	@SerializedName("udpated_at")
	private Date mUpdatedAt;

	@SerializedName("is_active")
	private boolean mActive;

	public String getId() {
		return mId;
	}

	public String getUserId() {
		return mUserId;
	}

	public boolean isEmailNotifications() {
		return (mEmailNotifications != 0);
	}

	public Date getCreatedAt() {
		return mCreatedAt;
	}

	public Date getUpdatedAt() {
		return mUpdatedAt;
	}

	public boolean isActive() {
		return mActive;
	}

	public void setEmailNotifications(boolean enable) {
		mEmailNotifications = (enable ? 1 : 0);
		setChanged();
		notifyObservers(enable);
	}
}
