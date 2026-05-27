package com.smartbookfinder.exception;

import com.smartbookfinder.dto.response.ApiErrorResponse;
import com.smartbookfinder.dto.response.ValidationErrorResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;
    private WebRequest request;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
        request = mock(WebRequest.class);
        when(request.getDescription(false)).thenReturn("uri=/api/test");
    }

    @Test
    void handleInvalidSearch_shouldReturn400() {
        InvalidSearchException ex = new InvalidSearchException("title or author required");

        ResponseEntity<ApiErrorResponse> response = handler.handleInvalidSearch(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().error()).isEqualTo("Invalid Search");
        assertThat(response.getBody().message()).isEqualTo("title or author required");
        assertThat(response.getBody().path()).isEqualTo("/api/test");
    }

    @Test
    void handleInsufficientResults_shouldReturn404() {
        InsufficientResultsException ex = new InsufficientResultsException("need at least 3 books");

        ResponseEntity<ApiErrorResponse> response = handler.handleInsufficientResults(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().error()).isEqualTo("Insufficient Results");
    }

    @Test
    void handleDuplicate_shouldReturn409() {
        DuplicateFavoriteException ex = new DuplicateFavoriteException("duplicate key");

        ResponseEntity<ApiErrorResponse> response = handler.handleDuplicate(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().error()).isEqualTo("Duplicate Favorite");
    }

    @Test
    void handleNotFound_shouldReturn404() {
        BookNotFoundException ex = new BookNotFoundException("book not found");

        ResponseEntity<ApiErrorResponse> response = handler.handleNotFound(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().error()).isEqualTo("Not Found");
    }

    @Test
    void handleExternalApi_shouldReturn502() {
        ExternalApiException ex = new ExternalApiException("OpenLibrary error");

        ResponseEntity<ApiErrorResponse> response = handler.handleExternalApi(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_GATEWAY);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().error()).isEqualTo("External API Error");
    }

    @Test
    void handleBeanValidation_shouldReturn400WithFieldErrors() throws Exception {
        Object target = new Object();
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(target, "target");
        bindingResult.addError(new FieldError("target", "title", "Title is required"));

        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(null, bindingResult);

        ResponseEntity<ValidationErrorResponse> response = handler.handleBeanValidation(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().error()).isEqualTo("Validation Failed");
        assertThat(response.getBody().fieldErrors()).containsKey("title");
        assertThat(response.getBody().fieldErrors().get("title")).isEqualTo("Title is required");
        assertThat(response.getBody().path()).isEqualTo("/api/test");
    }

    @Test
    void handleGeneric_shouldReturn500() {
        Exception ex = new RuntimeException("something went wrong");

        ResponseEntity<ApiErrorResponse> response = handler.handleGeneric(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().error()).isEqualTo("Internal Server Error");
        assertThat(response.getBody().message()).isEqualTo("An unexpected error occurred");
    }
}
