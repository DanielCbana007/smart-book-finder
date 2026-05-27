package com.smartbookfinder.dto.response;

import com.smartbookfinder.entity.SupportedLanguage;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;

@Schema(description = "A search history entry")
public record SearchHistoryResponse(
        @Schema(description = "History entry ID", example = "1")
        Long id,
        @Schema(description = "Title searched for", example = "Harry Potter")
        String titleQuery,
        @Schema(description = "Author searched for", example = "Rowling")
        String authorQuery,
        @Schema(description = "Language filter", example = "EN")
        SupportedLanguage language,
        @Schema(description = "Year filter", example = "2000")
        Integer publishedAfter,
        @Schema(description = "Number of results found", example = "42")
        Integer resultsCount,
        @Schema(description = "When the search was performed")
        Instant createdAt
) {}
