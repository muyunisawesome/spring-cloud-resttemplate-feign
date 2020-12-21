package com.cht.rst.feign.inner.logging;

/**
 * @author Clinton Begin
 */
public class LogException extends RuntimeException {

	private static final long serialVersionUID = 1022924004852350942L;

	public LogException() {
		super();
	}

	public LogException(String message) {
		super(message);
	}

	public LogException(String message, Throwable cause) {
		super(message, cause);
	}

	public LogException(Throwable cause) {
		super(cause);
	}

}
