package com.smartbookfinder.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExternalApiExceptionTest {

    @Test
    void constructor_shouldSetMessage() {
        ExternalApiException ex = new ExternalApiException("API error");
        assertThat(ex.getMessage()).isEqualTo("API error");
    }

    @Test
    void constructor_shouldSetMessageAndCause() {
        Throwable cause = new RuntimeException("connection timeout");
        ExternalApiException ex = new ExternalApiException("API error", cause);
        assertThat(ex.getMessage()).isEqualTo("API error");
        assertThat(ex.getCause()).isSameAs(cause);
    }
}
