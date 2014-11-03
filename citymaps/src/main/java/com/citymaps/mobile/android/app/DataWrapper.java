package com.citymaps.mobile.android.app;

/**
 * <p>A DataWrapper object wraps a data object.</p>
 * <p>Note that in this implementation, calls to getData will never result in a Throwable being thrown.</p>
 * @param <D> The type of the data this wrapper is wrapping.
 * @param <T> The type of the Throwable this wrapper is wrapping. Note that in this implementation,
 *           this throwable will never be thrown.
 */
public class DataWrapper<D, T extends Throwable> implements Wrapper<D, T> {

	private D mData;

	public DataWrapper(D data) {
		mData = data;
	}

	/**
	 * Returns the data being wrapped by this wrapper.
	 */
	@Override
	public D getData() throws T {
		return mData;
	}
}
