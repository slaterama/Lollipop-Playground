package com.citymaps.mobile.android.app;

/**
 * <p>A ThrowableWrapper object wraps a Throwable object.</p>
 * <p>Note that in this implementation, any call to getData will always result in a Throwable being thrown.</p>
 * @param <D> The type of the data this wrapper is wrapping.
 * @param <T> The type of the Throwable this wrapper is wrapping.
 */
public class ThrowableWrapper<D, T extends Throwable> implements Wrapper<D, T> {

	private T mThrowable;

	public ThrowableWrapper(T throwable) {
		mThrowable = throwable;
	}

	/**
	 * Throws the Throwable being wrapped by this wrapper.
	 */
	@Override
	public D getData() throws T {
		throw mThrowable;
	}
}
