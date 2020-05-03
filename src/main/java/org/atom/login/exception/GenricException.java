package org.atom.login.exception;

import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class GenricException extends RuntimeException{
	public GenricException(String message) {
		super(message);
	}

	public GenricException(String message, Throwable cause) {
		super(message, cause);
	}
}
