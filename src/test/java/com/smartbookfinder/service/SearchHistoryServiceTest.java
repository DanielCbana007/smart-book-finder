package com.smartbookfinder.service;

import com.smartbookfinder.dto.response.SearchHistoryResponse;
import com.smartbookfinder.entity.SearchHistory;
import com.smartbookfinder.entity.SupportedLanguage;
import com.smartbookfinder.repository.SearchHistoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SearchHistoryServiceTest {

    @Mock
    private SearchHistoryRepository repository;

    private SearchHistoryService service;

    @BeforeEach
    void setUp() {
        service = new SearchHistoryService(repository);
    }

    @Test
    void saveSearch_shouldSaveAndReturn() {
        SearchHistory saved = new SearchHistory();
        saved.setId(1L);
        saved.setTitleQuery("Dune");
        saved.setAuthorQuery("Frank Herbert");
        saved.setResultsCount(5);
        saved.setCreatedAt(Instant.now());

        when(repository.save(any())).thenReturn(saved);

        SearchHistory result = service.saveSearch("Dune", "Frank Herbert", null, null, 5);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getTitleQuery()).isEqualTo("Dune");
    }

    @Test
    void getHistory_shouldReturnPage() {
        SearchHistory history = new SearchHistory();
        history.setId(1L);
        history.setTitleQuery("Dune");
        history.setResultsCount(5);
        history.setCreatedAt(Instant.now());

        Page<SearchHistory> page = new PageImpl<>(List.of(history), PageRequest.of(0, 10), 1);
        when(repository.findAll(any(PageRequest.class))).thenReturn(page);

        Page<SearchHistoryResponse> result = service.getHistory(PageRequest.of(0, 10));

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).titleQuery()).isEqualTo("Dune");
    }

    @Test
    void getHistory_shouldReturnEmptyPage() {
        Page<SearchHistory> page = Page.empty();
        when(repository.findAll(any(PageRequest.class))).thenReturn(page);

        Page<SearchHistoryResponse> result = service.getHistory(PageRequest.of(0, 10));

        assertThat(result.getContent()).isEmpty();
    }

    @Test
    void saveSearch_shouldSaveWithNullFields() {
        SearchHistory saved = new SearchHistory();
        saved.setId(2L);
        saved.setResultsCount(0);
        saved.setCreatedAt(Instant.now());

        when(repository.save(any())).thenReturn(saved);

        SearchHistory result = service.saveSearch(null, null, null, null, 0);

        assertThat(result.getId()).isEqualTo(2L);
        assertThat(result.getTitleQuery()).isNull();
        assertThat(result.getAuthorQuery()).isNull();
        assertThat(result.getLanguage()).isNull();
        assertThat(result.getPublishedAfter()).isNull();
        assertThat(result.getResultsCount()).isZero();
        verify(repository).save(any());
    }

    @Test
    void getHistory_shouldMapAllFields() {
        SearchHistory history = new SearchHistory();
        history.setId(1L);
        history.setTitleQuery("Dune");
        history.setAuthorQuery("Frank Herbert");
        history.setLanguage(SupportedLanguage.EN);
        history.setPublishedAfter(1965);
        history.setResultsCount(10);
        history.setCreatedAt(Instant.parse("2025-01-01T00:00:00Z"));

        Page<SearchHistory> page = new PageImpl<>(List.of(history), PageRequest.of(0, 10), 1);
        when(repository.findAll(any(PageRequest.class))).thenReturn(page);

        Page<SearchHistoryResponse> result = service.getHistory(PageRequest.of(0, 10));

        assertThat(result.getContent()).hasSize(1);
        SearchHistoryResponse r = result.getContent().get(0);
        assertThat(r.id()).isEqualTo(1L);
        assertThat(r.titleQuery()).isEqualTo("Dune");
        assertThat(r.authorQuery()).isEqualTo("Frank Herbert");
        assertThat(r.language()).isEqualTo(SupportedLanguage.EN);
        assertThat(r.publishedAfter()).isEqualTo(1965);
        assertThat(r.resultsCount()).isEqualTo(10);
        assertThat(r.createdAt()).isEqualTo(Instant.parse("2025-01-01T00:00:00Z"));
    }

    @Test
    void getHistory_shouldMapWithNullFields() {
        SearchHistory history = new SearchHistory();
        history.setId(2L);
        history.setCreatedAt(Instant.now());

        Page<SearchHistory> page = new PageImpl<>(List.of(history), PageRequest.of(0, 10), 1);
        when(repository.findAll(any(PageRequest.class))).thenReturn(page);

        Page<SearchHistoryResponse> result = service.getHistory(PageRequest.of(0, 10));

        assertThat(result.getContent()).hasSize(1);
        SearchHistoryResponse r = result.getContent().get(0);
        assertThat(r.titleQuery()).isNull();
        assertThat(r.authorQuery()).isNull();
        assertThat(r.language()).isNull();
        assertThat(r.publishedAfter()).isNull();
        assertThat(r.resultsCount()).isNull();
    }
}