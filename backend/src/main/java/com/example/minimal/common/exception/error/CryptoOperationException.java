package com.example.minimal.common.exception.error;

public class CryptoOperationException extends RuntimeException {
	/**
	 * Serial version UID
	 */
	private static final long serialVersionUID = 1L;
	private final String field;
	private final String errorCode;

	public CryptoOperationException(String field, String message, String errorCode, Throwable cause) {
		super(message, cause);
		this.field = field;
		this.errorCode = errorCode;
	}

	public String getField() {
		return field;
	}

	public String getErrorCode() {
		return errorCode;
	}
}
