package com.citymaps.mobile.android.app;

/**
 * Base class for all (non-runtime) exceptions thrown by the Citymaps app.
 */
public class CitymapsException extends Exception {

	/**
	 * Constructs a new {@link CitymapsException} that includes the current stack trace.
	 */
	public CitymapsException() {
		super();
	}

	/**
	 * Constructs a new {@link CitymapsException} with the current stack trace and
	 * the specified detail message.
	 */
	public CitymapsException(String detailMessage) {
		super(detailMessage);
	}

	/**
	 * Constructs a new {@link CitymapsException} with the current stack trace,
	 * the specified detail message and the specified cause.
	 */
	public CitymapsException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}

	/**
	 * Constructs a new {@link CitymapsException} with the current stack trace
	 * and the specified cause.
	 */
	public CitymapsException(Throwable throwable) {
		super(throwable);
	}
}
