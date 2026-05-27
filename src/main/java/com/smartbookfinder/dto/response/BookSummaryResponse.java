package com.smartbookfinder.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Summary of a book from search results")
public record BookSummaryResponse(
        @Schema(description = "Book title", example = "Harry Potter and the Philosopher's Stone")
        String title,
        @Schema(description = "Author name", example = "J.K. Rowling")
        String author,
        @Schema(description = "Publication year", example = "1997")
        Integer publicationYear,
        @Schema(description = "Number of editions", example = "150")
        Integer editionsCount,
        @Schema(description = "URL to the cover image")
        String coverImageUrl,
        @Schema(description = "Unique book key from OpenLibrary", example = "/works/OL1234567W")
        String bookKey
) {}
