package com.citymaps.mobile.android.model.vo;

import android.content.Context;
import android.text.TextUtils;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.citymaps.mobile.android.app.SessionManager;
import com.citymaps.mobile.android.config.Endpoint;
import com.citymaps.mobile.android.model.CitymapsObject;
import com.citymaps.mobile.android.model.GetGsonRequest;
import com.citymaps.mobile.android.model.ResultWrapperV2;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * A user of the Citymaps application.
 * <p>Examples:
 * <ul>
 *     <li>Development: </li>
 * </ul>
 * </p>
 */
public class User implements CitymapsObject {

	/**
	 * The unique id assigned to this user.
	 */
	@SerializedName("user_id")
	protected String mId;

	/**
	 * The token assigned to this user by the Citymaps framework.
	 */
	@SerializedName("citymaps_token")
	protected String mCitymapsToken;

	/**
	 * The username chosen by this user.
	 */
	protected String mUsername;

	/**
	 * The user's first name.
	 */
	@SerializedName("first_name")
	protected String mFirstName;

	/**
	 * The user's last name.
	 */
	@SerializedName("last_name")
	protected String mLastName;

	/**
	 * The user's email address.
	 */
	protected String mEmail;

	/**
	 * Whether to use this user's company name as the display name.
	 */
	@SerializedName("display_company_name")
	protected boolean mDisplayCompany;

	/**
	 * This user's company name.
	 */
	@SerializedName("company_name")
	protected String mCompanyName;

	/**
	 * @return {@link CitymapsObject.ObjectType#USER}
	 */
	@Override
	public ObjectType getType() {
		return ObjectType.USER;
	}

	/**
	 * Sets the unique id assigned to this user.
	 * @param id The unique id assigned to this user.
	 */
	@Override
	public void setId(String id) {
		mId = id;
	}

	/**
	 * @return The unique id assigned to this user.
	 */
	@Override
	public String getId() {
		return mId;
	}

	/**
	 * @return The user's display name.
	 */
	@Override
	public String getName() {
		if (mDisplayCompany && !TextUtils.isEmpty(mCompanyName))
			return mCompanyName;
		return String.format("%s %s", mFirstName, mLastName).trim();
	}

	/**
	 * @return The token assigned to this user by the Citymaps framework.
	 */
	public String getCitymapsToken() {
		return mCitymapsToken;
	}

	/**
	 * Sets the token assigned to this user by the Citymaps framework.
	 * @param citymapsToken The token assigned to this user by the Citymaps framework.
	 */
	public void setCitymapsToken(String citymapsToken) {
		mCitymapsToken = citymapsToken;
	}

	/**
	 * @return The username chosen by this user.
	 */
	public String getUsername() {
		return mUsername;
	}

	/**
	 * Sets the username chosen by this user.
	 * @param username The username chosen by this user.
	 */
	public void setUsername(String username) {
		mUsername = username;
	}

	/**
	 * @return The user's first name.
	 */
	public String getFirstName() {
		return mFirstName;
	}

	/**
	 * @param firstName The user's first name.
	 */
	public void setFirstName(String firstName) {
		mFirstName = firstName;
	}

	/**
	 * @return The user's last name.
	 */
	public String getLastName() {
		return mLastName;
	}

	/**
	 * @param lastName The user's last name.
	 */
	public void setLastName(String lastName) {
		mLastName = lastName;
	}

	/**
	 * @return The user's email address.
	 */
	public String getEmail() {
		return mEmail;
	}

	/**
	 * Sets this user's email address.
	 * @param email The user's email address.
	 */
	public void setEmail(String email) {
		mEmail = email;
	}

	/**
	 * @return <code>True</code> to use this user's company name as the display name,
	 * <code>false</code> otherwise.
	 */
	public boolean isDisplayCompany() {
		return mDisplayCompany;
	}

	/**
	 * Sets whether to use this user's company name as the display name.
	 * @param displayCompany Whether to use this user's company name as the display name.
	 */
	public void setDisplayCompany(boolean displayCompany) {
		mDisplayCompany = displayCompany;
	}

	/**
	 * @return The user's company name.
	 */
	public String getCompanyName() {
		return mCompanyName;
	}

	/**
	 * Sets this user's company name.
	 * @param companyName The user's company name.
	 */
	public void setCompanyName(String companyName) {
		mCompanyName = companyName;
	}

	/**
	 * Indicates whether some other user is "equal to" this one.
	 * @param obj The reference user with which to compare.
	 * @return <code>True</code> if this user is the same as the obj argument; <code>false</code> otherwise.
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof User)
			return TextUtils.equals(mId, ((User) obj).getId());
		return super.equals(obj);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	public static class UserWrapper extends ResultWrapperV2 {
		@SerializedName("user")
		private User mUser;
	}

	public static class GetRequest extends GetGsonRequest<User> {

		public GetRequest(Context context, User currentUser, String userId,
							  Response.Listener<User> listener, Response.ErrorListener errorListener) {
			super(SessionManager.getEnvironment(context).buildUrlString(Endpoint.Type.USER, currentUser, userId),
					User.class, null, listener, errorListener);
		}

		@Override
		protected Response<User> parseNetworkResponse(NetworkResponse response) {
			Response<UserWrapper> parsedResponse = parseNetworkResponse(response, UserWrapper.class);
			return Response.success(parsedResponse.result.mUser, HttpHeaderParser.parseCacheHeaders(response));
		}
	}
}
