package com.smartbookfinder.service;

import com.smartbookfinder.dto.response.SearchHistoryResponse;
import com.smartbookfinder.entity.SearchHistory;
import com.smartbookfinder.entity.SupportedLanguage;
import com.smartbookfinder.repository.SearchHistoryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class SearchHistoryService {

    private final SearchHistoryRepository repository;

    public SearchHistoryService(SearchHistoryRepository repository) {
        this.repository = repository;
    }

    public SearchHistory saveSearch(String title, String author, SupportedLanguage language,
                                    Integer publishedAfter, int resultsCount) {
        SearchHistory history = new SearchHistory();
        history.setTitleQuery(title);
        history.setAuthorQuery(author);
        history.setLanguage(language);
        history.setPublishedAfter(publishedAfter);
        history.setResultsCount(resultsCount);
        return repository.save(history);
    }

    public Page<SearchHistoryResponse> getHistory(Pageable pageable) {
        return repository.findAll(pageable)
                .map(h -> new SearchHistoryResponse(
                        h.getId(),
                        h.getTitleQuery(),
                        h.getAuthorQuery(),
                        h.getLanguage(),
                        h.getPublishedAfter(),
                        h.getResultsCount(),
                        h.getCreatedAt()
                ));
    }
}
