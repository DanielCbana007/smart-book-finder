package com.smartbookfinder.service;

import com.smartbookfinder.dto.request.BookSearchRequest;
import com.smartbookfinder.exception.InvalidSearchException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Year;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BookSearchValidatorTest {

    private BookSearchValidator validator;

    @BeforeEach
    void setUp() {
        validator = new BookSearchValidator();
    }

    @Test
    void validateSearchRequest_validTitle_doesNotThrow() {
        BookSearchRequest request = new BookSearchRequest("Dune", null, null, null);
        assertThatNoException().isThrownBy(() -> validator.validateSearchRequest(request));
    }

    @Test
    void validateSearchRequest_validAuthor_doesNotThrow() {
        BookSearchRequest request = new BookSearchRequest(null, "Frank Herbert", null, null);
        assertThatNoException().isThrownBy(() -> validator.validateSearchRequest(request));
    }

    @Test
    void validateSearchRequest_bothTitleAndAuthor_doesNotThrow() {
        BookSearchRequest request = new BookSearchRequest("Dune", "Frank Herbert", null, null);
        assertThatNoException().isThrownBy(() -> validator.validateSearchRequest(request));
    }

    @Test
    void validateSearchRequest_empty_throwsInvalidSearchException() {
        BookSearchRequest request = new BookSearchRequest(null, null, null, null);
        assertThatThrownBy(() -> validator.validateSearchRequest(request))
                .isInstanceOf(InvalidSearchException.class)
                .hasMessageContaining("title");
    }

    @Test
    void validateSearchRequest_blankTitleAndAuthor_throwsInvalidSearchException() {
        BookSearchRequest request = new BookSearchRequest("", "  ", null, null);
        assertThatThrownBy(() -> validator.validateSearchRequest(request))
                .isInstanceOf(InvalidSearchException.class)
                .hasMessageContaining("title");
    }

    @Test
    void validateSearchRequest_publishedAfterInFuture_throwsInvalidSearchException() {
        int futureYear = Year.now().getValue() + 1;
        BookSearchRequest request = new BookSearchRequest("Dune", null, null, futureYear);
        assertThatThrownBy(() -> validator.validateSearchRequest(request))
                .isInstanceOf(InvalidSearchException.class)
                .hasMessageContaining("publishedAfter");
    }

    @Test
    void validateSearchRequest_publishedAfterCurrentYear_doesNotThrow() {
        BookSearchRequest request = new BookSearchRequest("Dune", null, null, Year.now().getValue());
        assertThatNoException().isThrownBy(() -> validator.validateSearchRequest(request));
    }

    @Test
    void validateSearchRequest_publishedAfterNull_doesNotThrow() {
        BookSearchRequest request = new BookSearchRequest("Dune", null, null, null);
        assertThatNoException().isThrownBy(() -> validator.validateSearchRequest(request));
    }

    @Test
    void validateSearchRequest_publishedAfterPastYear_doesNotThrow() {
        BookSearchRequest request = new BookSearchRequest("Dune", null, null, 2000);
        assertThatNoException().isThrownBy(() -> validator.validateSearchRequest(request));
    }
}