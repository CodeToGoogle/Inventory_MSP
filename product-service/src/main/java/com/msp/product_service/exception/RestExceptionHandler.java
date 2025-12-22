package com.msp.product_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.Instant;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(DuplicateSkuException.class)
    public final ResponseEntity<Object> handleDuplicateSkuException(DuplicateSkuException ex, WebRequest request) {
        ApiError apiError = new ApiError(Instant.now(), HttpStatus.CONFLICT.value(), "Conflict", ex.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(apiError, HttpStatus.CONFLICT);
    }
}
