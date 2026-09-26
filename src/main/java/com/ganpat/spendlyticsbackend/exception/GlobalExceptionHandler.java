package com.ganpat.spendlyticsbackend.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.ganpat.spendlyticsbackend.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice 
public class GlobalExceptionHandler {
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
        MethodArgumentNotValidException exception,
        HttpServletRequest request) {

            String message = exception.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(error -> error.getField() + ":" + error.getDefaultMessage())
            .collect(Collectors.joining(", "));

            ErrorResponse errorResponse = new  ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Bad request",
                message,
                request.getRequestURI());

            return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(errorResponse);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(
        ResourceNotFoundException exception,
        HttpServletRequest request
    ) {
        ErrorResponse response = new ErrorResponse(
            LocalDateTime.now(),
            HttpStatus.NOT_FOUND.value(),
            "Not found",
            exception.getMessage(),
            request.getRequestId()
        );
            return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(response);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgumentException(IllegalArgumentException exception) {
        Map<String, Object> response = new HashMap<>();
            response.put("status", 400);
            response.put("message", exception.getMessage());

            return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(response);
    }
}
