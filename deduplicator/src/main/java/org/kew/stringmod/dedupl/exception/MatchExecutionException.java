package org.kew.stringmod.dedupl.exception;

public class MatchExecutionException extends Exception {
	private static final long serialVersionUID = 1L;

	public MatchExecutionException() {
		super();
	}

	public MatchExecutionException(String message) {
		super(message);
	}

	public MatchExecutionException(String message, Throwable cause) {
		super(message, cause);
	}
}
