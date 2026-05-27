package com.smartbookfinder.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;

@Schema(description = "API error response")
public record ApiErrorResponse(
        @Schema(description = "Error timestamp")
        Instant timestamp,
        @Schema(description = "HTTP status code", example = "400")
        int status,
        @Schema(description = "Error type", example = "Bad Request")
        String error,
        @Schema(description = "Error message")
        String message,
        @Schema(description = "Request path", example = "/api/books/search")
        String path
) {}
