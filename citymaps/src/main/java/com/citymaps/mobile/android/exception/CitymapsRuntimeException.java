package com.citymaps.mobile.android.exception;

/**
 * Base class for all runtime exceptions thrown by the Citymaps app.
 */
public class CitymapsRuntimeException extends RuntimeException {

	/**
	 * Constructs a new {@link CitymapsRuntimeException} that includes the current stack trace.
	 */
	public CitymapsRuntimeException() {
		super();
	}

	/**
	 * Constructs a new {@link CitymapsRuntimeException} with the current stack trace and
	 * the specified detail message.
	 */
	public CitymapsRuntimeException(String detailMessage) {
		super(detailMessage);
	}

	/**
	 * Constructs a new {@link CitymapsRuntimeException} with the current stack trace,
	 * the specified detail message and the specified cause.
	 */
	public CitymapsRuntimeException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}

	/**
	 * Constructs a new {@link CitymapsConnectivityException} with the current stack trace
	 * and the specified cause.
	 */
	public CitymapsRuntimeException(Throwable throwable) {
		super(throwable);
	}
}
