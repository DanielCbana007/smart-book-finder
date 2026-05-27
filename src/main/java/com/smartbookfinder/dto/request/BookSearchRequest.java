package com.smartbookfinder.dto.request;

import com.smartbookfinder.entity.SupportedLanguage;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

@Schema(description = "Search request parameters")
public record BookSearchRequest(
        @Size(min = 1, max = 255)
        @Schema(description = "Book title to search for", example = "Harry Potter")
        String title,
        @Size(max = 255)
        @Schema(description = "Author name to filter by", example = "J.K. Rowling")
        String author,
        @Schema(description = "Language filter", example = "EN")
        SupportedLanguage language,
        @Schema(description = "Filter books published after this year", example = "2000")
        Integer publishedAfter
) {}
