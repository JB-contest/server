package com.example.jb.global.error;

import java.time.LocalDateTime;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {
	@ExceptionHandler(IllegalArgumentException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public Map<String, Object> badRequest(IllegalArgumentException exception) {
		return Map.of(
				"timestamp", LocalDateTime.now().toString(),
				"message", exception.getMessage()
		);
	}

	@ExceptionHandler(IllegalStateException.class)
	@ResponseStatus(HttpStatus.CONFLICT)
	public Map<String, Object> conflict(IllegalStateException exception) {
		return Map.of(
				"timestamp", LocalDateTime.now().toString(),
				"message", exception.getMessage()
		);
	}
}
