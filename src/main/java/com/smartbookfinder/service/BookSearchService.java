package com.smartbookfinder.service;

import com.smartbookfinder.client.OpenLibraryClient;
import com.smartbookfinder.client.OpenLibrarySearchResponse;
import com.smartbookfinder.dto.request.BookSearchRequest;
import com.smartbookfinder.dto.response.BookSearchResponse;
import com.smartbookfinder.dto.response.BookSummaryResponse;
import com.smartbookfinder.entity.SearchHistory;
import com.smartbookfinder.exception.InsufficientResultsException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class BookSearchService {

    private final OpenLibraryClient openLibraryClient;
    private final SearchHistoryService searchHistoryService;
    private final BookSearchValidator validator;
    private final int searchLimit;

    public BookSearchService(
            OpenLibraryClient openLibraryClient,
            SearchHistoryService searchHistoryService,
            BookSearchValidator validator,
            @Value("${openlibrary.search-limit}") int searchLimit
    ) {
        this.openLibraryClient = openLibraryClient;
        this.searchHistoryService = searchHistoryService;
        this.validator = validator;
        this.searchLimit = searchLimit;
    }

    public BookSearchResponse search(BookSearchRequest request) {
        validator.validateSearchRequest(request);

        OpenLibrarySearchResponse rawResponse = openLibraryClient.search(
                request.title(), request.author(), request.language(), searchLimit
        );

        List<OpenLibrarySearchResponse.OpenLibraryBook> docs = rawResponse.getDocs();

        if (docs == null || docs.isEmpty()) {
            throw new InsufficientResultsException("No books found matching the search criteria");
        }

        // Rule 5: Exclude books published before the requested year
        if (request.publishedAfter() != null) {
            docs = docs.stream()
                    .filter(book -> book.getFirstPublishYear() == null || book.getFirstPublishYear() >= request.publishedAfter())
                    .toList();
        }

        // Rule 4: Must have at least 3 matching results
        if (docs.size() < 3) {
            throw new InsufficientResultsException(
                    "Insufficient results: expected at least 3 books, but found " + docs.size()
            );
        }

        int totalMatching = docs.size();

        // Rule 4: Sort by editionCount descending, limit to top 3
        List<BookSummaryResponse> books = docs.stream()
                .sorted(Comparator.comparingInt(
                        (OpenLibrarySearchResponse.OpenLibraryBook b) ->
                                b.getEditionCount() != null ? b.getEditionCount() : 0
                ).reversed())
                .limit(3)
                .map(book -> new BookSummaryResponse(
                        book.getTitle(),
                        book.getAuthorName() != null ? String.join(", ", book.getAuthorName()) : null,
                        book.getFirstPublishYear(),
                        book.getEditionCount(),
                        book.getCoverId() != null
                                ? "https://covers.openlibrary.org/b/id/" + book.getCoverId() + "-M.jpg"
                                : null,
                        book.getKey()
                ))
                .toList();

        SearchHistory history = searchHistoryService.saveSearch(
                request.title(),
                request.author(),
                request.language(),
                request.publishedAfter(),
                totalMatching
        );

        return new BookSearchResponse(books, totalMatching, history.getId());
    }
}
