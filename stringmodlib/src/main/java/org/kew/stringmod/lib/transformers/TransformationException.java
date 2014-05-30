package org.kew.stringmod.lib.transformers;

public class TransformationException extends Exception {
	private static final long serialVersionUID = 1L;

	public TransformationException() {
		super();
	}

	public TransformationException(String message) {
		super(message);
	}

	public TransformationException(String message, Throwable cause) {
		super(message, cause);
	}
}
