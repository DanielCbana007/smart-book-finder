package com.smartbookfinder.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartbookfinder.dto.request.FavoriteBookRequest;
import com.smartbookfinder.dto.response.BookSearchResponse;
import com.smartbookfinder.dto.response.BookSummaryResponse;
import com.smartbookfinder.dto.response.FavoriteBookResponse;
import com.smartbookfinder.dto.response.SearchHistoryResponse;
import com.smartbookfinder.entity.SupportedLanguage;
import com.smartbookfinder.exception.*;
import com.smartbookfinder.service.BookSearchService;
import com.smartbookfinder.service.FavoriteBookService;
import com.smartbookfinder.service.SearchHistoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookSearchController.class)
class BookSearchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private BookSearchService bookSearchService;

    @MockitoBean
    private SearchHistoryService searchHistoryService;

    @MockitoBean
    private FavoriteBookService favoriteBookService;

    @Test
    void searchBooks_shouldReturn200() throws Exception {
        BookSearchResponse response = new BookSearchResponse(
                List.of(new BookSummaryResponse("Dune", "Frank Herbert", 1965, 100,
                        "https://covers.openlibrary.org/b/id/12345-M.jpg", "/works/OL123W")),
                1, 42L
        );

        when(bookSearchService.search(any())).thenReturn(response);

        mockMvc.perform(get("/api/books/search")
                        .param("title", "Dune"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.books[0].title").value("Dune"))
                .andExpect(jsonPath("$.totalResults").value(1))
                .andExpect(jsonPath("$.searchHistoryId").value(42));
    }

    @Test
    void searchBooks_shouldReturn200_withAllParams() throws Exception {
        BookSearchResponse response = new BookSearchResponse(List.of(), 0, 1L);
        when(bookSearchService.search(any())).thenReturn(response);

        mockMvc.perform(get("/api/books/search")
                        .param("title", "Dune")
                        .param("author", "Herbert")
                        .param("language", "EN")
                        .param("publishedAfter", "2000"))
                .andExpect(status().isOk());
    }

    @Test
    void searchBooks_shouldReturn400_whenNoParams() throws Exception {
        when(bookSearchService.search(any())).thenThrow(new InvalidSearchException("At least one of 'title' or 'author' must be provided"));

        mockMvc.perform(get("/api/books/search"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void searchBooks_shouldReturn404_whenInsufficientResults() throws Exception {
        when(bookSearchService.search(any())).thenThrow(new InsufficientResultsException("Insufficient results"));

        mockMvc.perform(get("/api/books/search")
                        .param("title", "RareBook"))
                .andExpect(status().isNotFound());
    }

    @Test
    void searchBooks_shouldReturn502_whenExternalApiFails() throws Exception {
        when(bookSearchService.search(any())).thenThrow(new ExternalApiException("OpenLibrary error"));

        mockMvc.perform(get("/api/books/search")
                        .param("title", "Dune"))
                .andExpect(status().isBadGateway());
    }

    @Test
    void saveFavorite_shouldReturn201() throws Exception {
        FavoriteBookRequest request = new FavoriteBookRequest(
                "/works/OL123W", "Dune", "Frank Herbert", 1965, 100,
                "https://covers.openlibrary.org/b/id/12345-M.jpg"
        );

        FavoriteBookResponse response = new FavoriteBookResponse(
                1L, "/works/OL123W", "Dune", "Frank Herbert",
                1965, 100, "https://covers.openlibrary.org/b/id/12345-M.jpg", Instant.now()
        );

        when(favoriteBookService.saveFavorite(any())).thenReturn(response);

        mockMvc.perform(post("/api/favorites")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Dune"));
    }

    @Test
    void saveFavorite_shouldReturn400_whenInvalid() throws Exception {
        FavoriteBookRequest request = new FavoriteBookRequest(
                "", "", null, null, null, null
        );

        mockMvc.perform(post("/api/favorites")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void saveFavorite_shouldReturn409_whenDuplicate() throws Exception {
        FavoriteBookRequest request = new FavoriteBookRequest(
                "/works/OL123W", "Dune", "Frank Herbert", 1965, 100, null
        );

        when(favoriteBookService.saveFavorite(any()))
                .thenThrow(new DuplicateFavoriteException("Book already in favorites"));

        mockMvc.perform(post("/api/favorites")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    void listFavorites_shouldReturn200() throws Exception {
        when(favoriteBookService.getAllFavorites()).thenReturn(List.of());

        mockMvc.perform(get("/api/favorites"))
                .andExpect(status().isOk());
    }

    @Test
    void deleteFavorite_shouldReturn204() throws Exception {
        mockMvc.perform(delete("/api/favorites/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteFavorite_shouldReturn404_whenNotFound() throws Exception {
        doThrow(new BookNotFoundException("Favorite book with id '99' not found"))
                .when(favoriteBookService).deleteFavorite(99L);

        mockMvc.perform(delete("/api/favorites/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getHistory_shouldReturn200() throws Exception {
        Page<SearchHistoryResponse> page = new PageImpl<>(
                List.of(new SearchHistoryResponse(1L, "Dune", null, null, null, 5, Instant.now())),
                PageRequest.of(0, 20), 1
        );

        when(searchHistoryService.getHistory(any())).thenReturn(page);

        mockMvc.perform(get("/api/history"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].titleQuery").value("Dune"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }
}