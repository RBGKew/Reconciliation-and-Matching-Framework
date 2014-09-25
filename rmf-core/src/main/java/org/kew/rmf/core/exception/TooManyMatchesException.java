package org.kew.rmf.core.exception;

public class TooManyMatchesException extends Exception {
	private static final long serialVersionUID = 1L;

	public TooManyMatchesException() {
		super();
	}

	public TooManyMatchesException(String message) {
		super(message);
	}

	public TooManyMatchesException(String message, Throwable cause) {
		super(message, cause);
	}
}
