package com.smartbookfinder.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "Response containing search results")
public record BookSearchResponse(
        @Schema(description = "List of books found")
        List<BookSummaryResponse> books,
        @Schema(description = "Total number of results", example = "42")
        int totalResults,
        @Schema(description = "ID of the saved search history entry", example = "1")
        Long searchHistoryId
) {}
