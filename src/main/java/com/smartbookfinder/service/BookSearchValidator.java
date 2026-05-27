package com.smartbookfinder.service;

import com.smartbookfinder.dto.request.BookSearchRequest;
import com.smartbookfinder.exception.InvalidSearchException;
import org.springframework.stereotype.Component;

import java.time.Year;

@Component
public class BookSearchValidator {

    public void validateSearchRequest(BookSearchRequest request) {
        boolean hasTitle = request.title() != null && !request.title().isBlank();
        boolean hasAuthor = request.author() != null && !request.author().isBlank();

        if (!hasTitle && !hasAuthor) {
            throw new InvalidSearchException("At least one of 'title' or 'author' must be provided");
        }

        if (request.publishedAfter() != null && request.publishedAfter() > Year.now().getValue()) {
            throw new InvalidSearchException("publishedAfter cannot be greater than the current year");
        }
    }
}
