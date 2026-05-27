package com.smartbookfinder.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Request body to save a book as favorite")
public record FavoriteBookRequest(
        @NotBlank
        @Size(max = 255)
        @Schema(description = "Unique book key from OpenLibrary", example = "/works/OL1234567W")
        String bookKey,
        @NotBlank
        @Size(max = 512)
        @Schema(description = "Book title", example = "Harry Potter and the Philosopher's Stone")
        String title,
        @Size(max = 255)
        @Schema(description = "Book author", example = "J.K. Rowling")
        String author,
        @Schema(description = "Publication year", example = "1997")
        Integer publicationYear,
        @Schema(description = "Number of editions", example = "150")
        Integer editionsCount,
        @Size(max = 2048)
        @Schema(description = "URL to the cover image")
        String coverImageUrl
) {}
