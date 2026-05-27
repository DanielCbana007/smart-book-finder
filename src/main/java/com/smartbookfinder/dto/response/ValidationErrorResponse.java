package com.smartbookfinder.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.Map;

@Schema(description = "Validation error response with field-level details")
public record ValidationErrorResponse(
        @Schema(description = "Error timestamp")
        Instant timestamp,
        @Schema(description = "HTTP status code", example = "400")
        int status,
        @Schema(description = "Error type", example = "Validation Failed")
        String error,
        @Schema(description = "Request path")
        String path,
        @Schema(description = "Field-level validation errors")
        Map<String, String> fieldErrors
) {}
