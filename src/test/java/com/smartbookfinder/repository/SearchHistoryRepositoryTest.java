package com.smartbookfinder.repository;

import com.smartbookfinder.entity.SearchHistory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class SearchHistoryRepositoryTest {

    @Autowired
    private SearchHistoryRepository repository;

    @Test
    void shouldSaveAndFindAll() {
        SearchHistory history = new SearchHistory();
        history.setTitleQuery("Dune");
        history.setAuthorQuery("Frank Herbert");
        history.setResultsCount(5);
        repository.save(history);

        List<SearchHistory> all = repository.findAll();

        assertThat(all).hasSize(1);
        assertThat(all.get(0).getTitleQuery()).isEqualTo("Dune");
    }

    @Test
    void shouldSaveWithAllFields() {
        SearchHistory history = new SearchHistory();
        history.setTitleQuery("Harry Potter");
        history.setAuthorQuery("Rowling");
        history.setPublishedAfter(2000);
        history.setResultsCount(10);
        history = repository.save(history);

        assertThat(history.getId()).isNotNull();
        assertThat(history.getCreatedAt()).isNotNull();
    }

    @Test
    void shouldReturnEmptyWhenNoHistory() {
        List<SearchHistory> all = repository.findAll();
        assertThat(all).isEmpty();
    }
}