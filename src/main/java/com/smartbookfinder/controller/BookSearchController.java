package com.smartbookfinder.controller;

import com.smartbookfinder.dto.request.BookSearchRequest;
import com.smartbookfinder.dto.request.FavoriteBookRequest;
import com.smartbookfinder.dto.response.BookSearchResponse;
import com.smartbookfinder.dto.response.FavoriteBookResponse;
import com.smartbookfinder.dto.response.SearchHistoryResponse;
import com.smartbookfinder.service.BookSearchService;
import com.smartbookfinder.service.FavoriteBookService;
import com.smartbookfinder.service.SearchHistoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@Tag(name = "Books", description = "Endpoints for searching books and managing favorites & search history")
public class BookSearchController {

    private final BookSearchService bookSearchService;
    private final SearchHistoryService searchHistoryService;
    private final FavoriteBookService favoriteBookService;

    public BookSearchController(
            BookSearchService bookSearchService,
            SearchHistoryService searchHistoryService,
            FavoriteBookService favoriteBookService
    ) {
        this.bookSearchService = bookSearchService;
        this.searchHistoryService = searchHistoryService;
        this.favoriteBookService = favoriteBookService;
    }

    @GetMapping("/books/search")
    @Operation(summary = "Search books", description = "Searches books from OpenLibrary by title, author, language, or year")
    public ResponseEntity<BookSearchResponse> searchBooks(@Valid BookSearchRequest request) {
        BookSearchResponse response = bookSearchService.search(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/favorites")
    @Operation(summary = "Save a favorite book")
    public ResponseEntity<FavoriteBookResponse> saveFavorite(@Valid @RequestBody FavoriteBookRequest request) {
        FavoriteBookResponse response = favoriteBookService.saveFavorite(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/favorites")
    @Operation(summary = "List all favorite books")
    public ResponseEntity<List<FavoriteBookResponse>> listFavorites() {
        List<FavoriteBookResponse> favorites = favoriteBookService.getAllFavorites();
        return ResponseEntity.ok(favorites);
    }

    @DeleteMapping("/favorites/{id}")
    @Operation(summary = "Delete a favorite book by ID")
    public ResponseEntity<Void> deleteFavorite(@Parameter(description = "Favorite book ID") @PathVariable Long id) {
        favoriteBookService.deleteFavorite(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/history")
    @Operation(summary = "Get search history", description = "Returns paginated search history")
    public ResponseEntity<Page<SearchHistoryResponse>> getHistory(Pageable pageable) {
        Page<SearchHistoryResponse> history = searchHistoryService.getHistory(pageable);
        return ResponseEntity.ok(history);
    }
}
