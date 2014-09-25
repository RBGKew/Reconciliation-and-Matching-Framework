package org.kew.rmf.core.exception;

public class DataLoadException extends Exception {
	private static final long serialVersionUID = 1L;

	public DataLoadException() {
		super();
	}

	public DataLoadException(String message) {
		super(message);
	}

	public DataLoadException(String message, Throwable cause) {
		super(message, cause);
	}
}
