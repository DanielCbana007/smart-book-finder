package com.smartbookfinder.service;

import com.smartbookfinder.dto.request.FavoriteBookRequest;
import com.smartbookfinder.dto.response.FavoriteBookResponse;
import com.smartbookfinder.entity.FavoriteBook;
import com.smartbookfinder.exception.BookNotFoundException;
import com.smartbookfinder.exception.DuplicateFavoriteException;
import com.smartbookfinder.repository.FavoriteBookRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FavoriteBookService {

    private final FavoriteBookRepository repository;

    public FavoriteBookService(FavoriteBookRepository repository) {
        this.repository = repository;
    }

    public FavoriteBookResponse saveFavorite(FavoriteBookRequest request) {
        if (repository.existsByBookKey(request.bookKey())) {
            throw new DuplicateFavoriteException(
                    "Book with key '" + request.bookKey() + "' is already in favorites"
            );
        }

        FavoriteBook book = new FavoriteBook();
        book.setBookKey(request.bookKey());
        book.setTitle(request.title());
        book.setAuthor(request.author());
        book.setPublicationYear(request.publicationYear());
        book.setEditionsCount(request.editionsCount());
        book.setCoverImageUrl(request.coverImageUrl());
        book = repository.save(book);

        return toResponse(book);
    }

    public List<FavoriteBookResponse> getAllFavorites() {
        return repository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public void deleteFavorite(Long id) {
        if (!repository.existsById(id)) {
            throw new BookNotFoundException("Favorite book with id '" + id + "' not found");
        }
        repository.deleteById(id);
    }

    private FavoriteBookResponse toResponse(FavoriteBook book) {
        return new FavoriteBookResponse(
                book.getId(),
                book.getBookKey(),
                book.getTitle(),
                book.getAuthor(),
                book.getPublicationYear(),
                book.getEditionsCount(),
                book.getCoverImageUrl(),
                book.getCreatedAt()
        );
    }
}
