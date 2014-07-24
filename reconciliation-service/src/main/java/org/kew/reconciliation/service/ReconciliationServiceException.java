package org.kew.reconciliation.service;

public class ReconciliationServiceException extends Exception {
	private static final long serialVersionUID = 1L;

	public ReconciliationServiceException() {
		super();
	}

	public ReconciliationServiceException(String message) {
		super(message);
	}

	public ReconciliationServiceException(String message, Throwable cause) {
		super(message, cause);
	}
}
