package com.smartbookfinder.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;

@Schema(description = "A favorited book")
public record FavoriteBookResponse(
        @Schema(description = "Favorite ID", example = "1")
        Long id,
        @Schema(description = "Unique book key from OpenLibrary", example = "/works/OL1234567W")
        String bookKey,
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
        @Schema(description = "When it was favorited")
        Instant createdAt
) {}
