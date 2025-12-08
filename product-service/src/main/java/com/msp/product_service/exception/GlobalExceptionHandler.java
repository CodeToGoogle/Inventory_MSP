package com.msp.product_service.exception;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.dao.DataIntegrityViolationException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiError> handleRuntime(RuntimeException ex, WebRequest req) {
        ApiError err = ApiError.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Bad Request")
                .message(ex.getMessage())
                .path(req.getDescription(false))
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> handleDup(DataIntegrityViolationException ex, WebRequest req) {
        ApiError err = ApiError.builder()
                .status(HttpStatus.CONFLICT.value())
                .error("Data Integrity")
                .message(ex.getRootCause() != null ? ex.getRootCause().getMessage() : ex.getMessage())
                .path(req.getDescription(false))
                .build();
        return ResponseEntity.status(HttpStatus.CONFLICT).body(err);
    }
}

