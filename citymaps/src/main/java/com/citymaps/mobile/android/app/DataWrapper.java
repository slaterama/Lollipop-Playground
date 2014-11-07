package com.citymaps.mobile.android.app;

import com.citymaps.mobile.android.exception.CitymapsException;

/**
 * <p>A DataWrapper object wraps a data object.</p>
 * <p>Note that in this implementation, calls to getData will never result in a Citymaps Exception being thrown.</p>
 * @param <D> The type of the data this wrapper is wrapping.
 */
public class DataWrapper<D> implements Wrapper<D> {

	private D mData;

	public DataWrapper(D data) {
		mData = data;
	}

	/**
	 * Returns the data being wrapped by this wrapper.
	 */
	@Override
	public D getData() throws CitymapsException {
		return mData;
	}
}
