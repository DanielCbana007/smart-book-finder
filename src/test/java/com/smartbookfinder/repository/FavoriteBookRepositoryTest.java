package com.smartbookfinder.repository;

import com.smartbookfinder.entity.FavoriteBook;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class FavoriteBookRepositoryTest {

    @Autowired
    private FavoriteBookRepository repository;

    @Test
    void shouldSaveAndFindByBookKey() {
        FavoriteBook book = new FavoriteBook();
        book.setBookKey("/works/OL123W");
        book.setTitle("Dune");
        book.setAuthor("Frank Herbert");
        repository.save(book);

        Optional<FavoriteBook> found = repository.findByBookKey("/works/OL123W");

        assertThat(found).isPresent();
        assertThat(found.get().getTitle()).isEqualTo("Dune");
    }

    @Test
    void shouldReturnTrueWhenExistsByBookKey() {
        FavoriteBook book = new FavoriteBook();
        book.setBookKey("/works/OL456W");
        book.setTitle("1984");
        repository.save(book);

        boolean exists = repository.existsByBookKey("/works/OL456W");

        assertThat(exists).isTrue();
    }

    @Test
    void shouldReturnFalseWhenNotExistsByBookKey() {
        boolean exists = repository.existsByBookKey("/works/NONEXISTENT");

        assertThat(exists).isFalse();
    }

    @Test
    void shouldDeleteById() {
        FavoriteBook book = new FavoriteBook();
        book.setBookKey("/works/OL789W");
        book.setTitle("Foundation");
        book = repository.save(book);

        repository.deleteById(book.getId());

        assertThat(repository.existsById(book.getId())).isFalse();
    }
}
