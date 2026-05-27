package com.smartbookfinder.exception;

import com.smartbookfinder.dto.response.ApiErrorResponse;
import com.smartbookfinder.dto.response.ValidationErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidSearchException.class)
    public ResponseEntity<ApiErrorResponse> handleInvalidSearch(InvalidSearchException ex, WebRequest request) {
        return buildResponse(HttpStatus.BAD_REQUEST, "Invalid Search", ex.getMessage(), request);
    }

    @ExceptionHandler(InsufficientResultsException.class)
    public ResponseEntity<ApiErrorResponse> handleInsufficientResults(InsufficientResultsException ex, WebRequest request) {
        return buildResponse(HttpStatus.NOT_FOUND, "Insufficient Results", ex.getMessage(), request);
    }

    @ExceptionHandler(DuplicateFavoriteException.class)
    public ResponseEntity<ApiErrorResponse> handleDuplicate(DuplicateFavoriteException ex, WebRequest request) {
        return buildResponse(HttpStatus.CONFLICT, "Duplicate Favorite", ex.getMessage(), request);
    }

    @ExceptionHandler(BookNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNotFound(BookNotFoundException ex, WebRequest request) {
        return buildResponse(HttpStatus.NOT_FOUND, "Not Found", ex.getMessage(), request);
    }

    @ExceptionHandler(ExternalApiException.class)
    public ResponseEntity<ApiErrorResponse> handleExternalApi(ExternalApiException ex, WebRequest request) {
        return buildResponse(HttpStatus.BAD_GATEWAY, "External API Error", ex.getMessage(), request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleBeanValidation(
            MethodArgumentNotValidException ex, WebRequest request
    ) {
        Map<String, String> fieldErrors = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(fe ->
                fieldErrors.put(fe.getField(), fe.getDefaultMessage())
        );

        ValidationErrorResponse body = new ValidationErrorResponse(
                Instant.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Validation Failed",
                request.getDescription(false).replace("uri=", ""),
                fieldErrors
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGeneric(Exception ex, WebRequest request) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error",
                "An unexpected error occurred", request);
    }

    private ResponseEntity<ApiErrorResponse> buildResponse(
            HttpStatus status, String error, String message, WebRequest request
    ) {
        ApiErrorResponse body = new ApiErrorResponse(
                Instant.now(),
                status.value(),
                error,
                message,
                request.getDescription(false).replace("uri=", "")
        );
        return ResponseEntity.status(status).body(body);
    }
}
