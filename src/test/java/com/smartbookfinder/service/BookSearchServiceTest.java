package com.smartbookfinder.service;

import com.smartbookfinder.client.OpenLibraryClient;
import com.smartbookfinder.client.OpenLibrarySearchResponse;
import com.smartbookfinder.dto.request.BookSearchRequest;
import com.smartbookfinder.dto.response.BookSearchResponse;
import com.smartbookfinder.entity.SearchHistory;
import com.smartbookfinder.entity.SupportedLanguage;
import com.smartbookfinder.exception.InsufficientResultsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookSearchServiceTest {

    @Mock
    private OpenLibraryClient openLibraryClient;

    @Mock
    private SearchHistoryService searchHistoryService;

    @Mock
    private BookSearchValidator validator;

    private BookSearchService service;

    @BeforeEach
    void setUp() {
        service = new BookSearchService(openLibraryClient, searchHistoryService, validator, 20);
    }

    private OpenLibrarySearchResponse.OpenLibraryBook createBook(String title, String author, Integer year, Integer editions, Integer coverId, String key) {
        OpenLibrarySearchResponse.OpenLibraryBook book = new OpenLibrarySearchResponse.OpenLibraryBook();
        book.setTitle(title);
        book.setAuthorName(author != null ? List.of(author) : null);
        book.setFirstPublishYear(year);
        book.setEditionCount(editions);
        book.setCoverId(coverId);
        book.setKey(key);
        return book;
    }

    @Test
    void search_shouldReturnBooks_whenValidRequest() {
        BookSearchRequest request = new BookSearchRequest("Dune", null, null, null);

        OpenLibrarySearchResponse rawResponse = new OpenLibrarySearchResponse();
        rawResponse.setNumFound(10);
        rawResponse.setDocs(List.of(
                createBook("Dune", "Frank Herbert", 1965, 100, 12345, "/works/OL123W"),
                createBook("Dune Messiah", "Frank Herbert", 1969, 50, 12346, "/works/OL124W"),
                createBook("Children of Dune", "Frank Herbert", 1976, 40, 12347, "/works/OL125W")
        ));

        SearchHistory history = new SearchHistory();
        history.setId(42L);

        when(openLibraryClient.search(any(), any(), any(), anyInt())).thenReturn(rawResponse);
        when(searchHistoryService.saveSearch(any(), any(), any(), any(), anyInt())).thenReturn(history);

        BookSearchResponse response = service.search(request);

        assertThat(response.books()).hasSize(3);
        assertThat(response.books().get(0).title()).isEqualTo("Dune");
        assertThat(response.totalResults()).isEqualTo(3);
        assertThat(response.searchHistoryId()).isEqualTo(42L);
    }

    @Test
    void search_shouldThrowInsufficientResults_whenNoDocsReturned() {
        BookSearchRequest request = new BookSearchRequest("Nonexistent Book XYZ", null, null, null);

        OpenLibrarySearchResponse rawResponse = new OpenLibrarySearchResponse();
        rawResponse.setNumFound(0);
        rawResponse.setDocs(List.of());

        when(openLibraryClient.search(any(), any(), any(), anyInt())).thenReturn(rawResponse);

        assertThatThrownBy(() -> service.search(request))
                .isInstanceOf(InsufficientResultsException.class)
                .hasMessageContaining("No books found");
    }

    @Test
    void search_shouldThrowInsufficientResults_whenDocsIsNull() {
        BookSearchRequest request = new BookSearchRequest("Test", null, null, null);

        OpenLibrarySearchResponse rawResponse = new OpenLibrarySearchResponse();
        rawResponse.setNumFound(0);
        rawResponse.setDocs(null);

        when(openLibraryClient.search(any(), any(), any(), anyInt())).thenReturn(rawResponse);

        assertThatThrownBy(() -> service.search(request))
                .isInstanceOf(InsufficientResultsException.class);
    }

    @Test
    void search_shouldThrowInsufficientResults_whenLessThan3Results() {
        BookSearchRequest request = new BookSearchRequest("Test", null, null, null);

        OpenLibrarySearchResponse rawResponse = new OpenLibrarySearchResponse();
        rawResponse.setDocs(List.of(
                createBook("Book 1", "Author 1", 2000, 10, 1, "/works/OL1W"),
                createBook("Book 2", "Author 2", 2001, 5, 2, "/works/OL2W")
        ));

        when(openLibraryClient.search(any(), any(), any(), anyInt())).thenReturn(rawResponse);

        assertThatThrownBy(() -> service.search(request))
                .isInstanceOf(InsufficientResultsException.class)
                .hasMessageContaining("expected at least 3");
    }

    @Test
    void search_shouldFilterByPublishedAfter() {
        BookSearchRequest request = new BookSearchRequest("Dune", null, null, 1970);

        OpenLibrarySearchResponse rawResponse = new OpenLibrarySearchResponse();
        rawResponse.setDocs(List.of(
                createBook("Dune", "Frank Herbert", 1965, 100, 12345, "/works/OL123W"),
                createBook("Dune Messiah", "Frank Herbert", 1969, 50, 12346, "/works/OL124W"),
                createBook("Children of Dune", "Frank Herbert", 1976, 40, 12347, "/works/OL125W"),
                createBook("God Emperor of Dune", "Frank Herbert", 1981, 30, 12348, "/works/OL126W"),
                createBook("Heretics of Dune", "Frank Herbert", 1984, 25, 12349, "/works/OL127W")
        ));

        SearchHistory history = new SearchHistory();
        history.setId(1L);

        when(openLibraryClient.search(any(), any(), any(), anyInt())).thenReturn(rawResponse);
        when(searchHistoryService.saveSearch(any(), any(), any(), any(), anyInt())).thenReturn(history);

        BookSearchResponse response = service.search(request);

        assertThat(response.books()).hasSize(3);
        assertThat(response.books().get(0).title()).isEqualTo("Children of Dune");
        assertThat(response.books().get(1).title()).isEqualTo("God Emperor of Dune");
        assertThat(response.books().get(2).title()).isEqualTo("Heretics of Dune");
        assertThat(response.totalResults()).isEqualTo(3);
    }

    @Test
    void search_shouldSortByEditionCountDesc() {
        BookSearchRequest request = new BookSearchRequest("Dune", null, null, null);

        OpenLibrarySearchResponse rawResponse = new OpenLibrarySearchResponse();
        rawResponse.setDocs(List.of(
                createBook("Book A", "Author", 2000, 5, 1, "/works/OL1W"),
                createBook("Book B", "Author", 2001, 100, 2, "/works/OL2W"),
                createBook("Book C", "Author", 2002, 50, 3, "/works/OL3W"),
                createBook("Book D", "Author", 2003, 200, 4, "/works/OL4W")
        ));

        SearchHistory history = new SearchHistory();
        history.setId(1L);

        when(openLibraryClient.search(any(), any(), any(), anyInt())).thenReturn(rawResponse);
        when(searchHistoryService.saveSearch(any(), any(), any(), any(), anyInt())).thenReturn(history);

        BookSearchResponse response = service.search(request);

        assertThat(response.books()).hasSize(3);
        assertThat(response.books().get(0).editionsCount()).isEqualTo(200);
        assertThat(response.books().get(1).editionsCount()).isEqualTo(100);
        assertThat(response.books().get(2).editionsCount()).isEqualTo(50);
    }

    @Test
    void search_shouldHandleNullEditionCountInSorting() {
        BookSearchRequest request = new BookSearchRequest("Test", null, null, null);

        OpenLibrarySearchResponse rawResponse = new OpenLibrarySearchResponse();
        rawResponse.setDocs(List.of(
                createBook("Book A", "Author", 2000, null, 1, "/works/OL1W"),
                createBook("Book B", "Author", 2001, 10, 2, "/works/OL2W"),
                createBook("Book C", "Author", 2002, 5, 3, "/works/OL3W")
        ));

        SearchHistory history = new SearchHistory();
        history.setId(1L);

        when(openLibraryClient.search(any(), any(), any(), anyInt())).thenReturn(rawResponse);
        when(searchHistoryService.saveSearch(any(), any(), any(), any(), anyInt())).thenReturn(history);

        BookSearchResponse response = service.search(request);

        assertThat(response.books()).hasSize(3);
        assertThat(response.books().get(0).editionsCount()).isEqualTo(10);
        assertThat(response.books().get(1).editionsCount()).isEqualTo(5);
        assertThat(response.books().get(2).editionsCount()).isNull();
    }

    @Test
    void search_shouldFilterByPublishedAfterAndThrowWhenInsufficient() {
        BookSearchRequest request = new BookSearchRequest("Test", null, null, 2000);

        OpenLibrarySearchResponse rawResponse = new OpenLibrarySearchResponse();
        rawResponse.setDocs(List.of(
                createBook("Book 1", "Author", 1990, 10, 1, "/works/OL1W"),
                createBook("Book 2", "Author", 1995, 5, 2, "/works/OL2W")
        ));

        when(openLibraryClient.search(any(), any(), any(), anyInt())).thenReturn(rawResponse);

        assertThatThrownBy(() -> service.search(request))
                .isInstanceOf(InsufficientResultsException.class)
                .hasMessageContaining("expected at least 3");
    }

    @Test
    void search_shouldPassLanguageToClient() {
        BookSearchRequest request = new BookSearchRequest("Dune", null, SupportedLanguage.ES, null);

        OpenLibrarySearchResponse rawResponse = new OpenLibrarySearchResponse();
        rawResponse.setDocs(List.of(
                createBook("Dune", "Frank Herbert", 1965, 100, 12345, "/works/OL123W"),
                createBook("Dune Messiah", "Frank Herbert", 1969, 50, 12346, "/works/OL124W"),
                createBook("Children of Dune", "Frank Herbert", 1976, 40, 12347, "/works/OL125W")
        ));

        SearchHistory history = new SearchHistory();
        history.setId(1L);

        when(openLibraryClient.search(any(), any(), eq(SupportedLanguage.ES), anyInt())).thenReturn(rawResponse);
        when(searchHistoryService.saveSearch(any(), any(), any(), any(), anyInt())).thenReturn(history);

        BookSearchResponse response = service.search(request);

        assertThat(response.books()).hasSize(3);
    }
}