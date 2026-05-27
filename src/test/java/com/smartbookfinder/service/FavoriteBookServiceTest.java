package com.smartbookfinder.service;

import com.smartbookfinder.dto.request.FavoriteBookRequest;
import com.smartbookfinder.dto.response.FavoriteBookResponse;
import com.smartbookfinder.entity.FavoriteBook;
import com.smartbookfinder.exception.BookNotFoundException;
import com.smartbookfinder.exception.DuplicateFavoriteException;
import com.smartbookfinder.repository.FavoriteBookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FavoriteBookServiceTest {

    @Mock
    private FavoriteBookRepository repository;

    private FavoriteBookService service;

    @BeforeEach
    void setUp() {
        service = new FavoriteBookService(repository);
    }

    @Test
    void saveFavorite_shouldSaveAndReturnResponse() {
        FavoriteBookRequest request = new FavoriteBookRequest(
                "/works/OL123W", "Dune", "Frank Herbert",
                1965, 100, "https://covers.openlibrary.org/b/id/12345-M.jpg"
        );

        FavoriteBook savedBook = new FavoriteBook();
        savedBook.setId(1L);
        savedBook.setBookKey("/works/OL123W");
        savedBook.setTitle("Dune");
        savedBook.setAuthor("Frank Herbert");
        savedBook.setPublicationYear(1965);
        savedBook.setEditionsCount(100);
        savedBook.setCoverImageUrl("https://covers.openlibrary.org/b/id/12345-M.jpg");
        savedBook.setCreatedAt(Instant.now());

        when(repository.existsByBookKey("/works/OL123W")).thenReturn(false);
        when(repository.save(any())).thenReturn(savedBook);

        FavoriteBookResponse response = service.saveFavorite(request);

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.title()).isEqualTo("Dune");
        assertThat(response.bookKey()).isEqualTo("/works/OL123W");
    }

    @Test
    void saveFavorite_shouldThrow_whenDuplicate() {
        FavoriteBookRequest request = new FavoriteBookRequest(
                "/works/OL123W", "Dune", "Frank Herbert",
                1965, 100, null
        );

        when(repository.existsByBookKey("/works/OL123W")).thenReturn(true);

        assertThatThrownBy(() -> service.saveFavorite(request))
                .isInstanceOf(DuplicateFavoriteException.class)
                .hasMessageContaining("already in favorites");

        verify(repository, never()).save(any());
    }

    @Test
    void getAllFavorites_shouldReturnList() {
        FavoriteBook book = new FavoriteBook();
        book.setId(1L);
        book.setBookKey("/works/OL123W");
        book.setTitle("Dune");
        book.setCreatedAt(Instant.now());

        when(repository.findAll()).thenReturn(List.of(book));

        List<FavoriteBookResponse> result = service.getAllFavorites();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).title()).isEqualTo("Dune");
    }

    @Test
    void getAllFavorites_shouldReturnEmptyList() {
        when(repository.findAll()).thenReturn(List.of());

        List<FavoriteBookResponse> result = service.getAllFavorites();

        assertThat(result).isEmpty();
    }

    @Test
    void deleteFavorite_shouldDelete() {
        when(repository.existsById(1L)).thenReturn(true);

        service.deleteFavorite(1L);

        verify(repository).deleteById(1L);
    }

    @Test
    void deleteFavorite_shouldThrow_whenNotFound() {
        when(repository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> service.deleteFavorite(99L))
                .isInstanceOf(BookNotFoundException.class)
                .hasMessageContaining("not found");

        verify(repository, never()).deleteById(any());
    }
}