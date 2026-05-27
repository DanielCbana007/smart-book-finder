package com.smartbookfinder.repository;

import com.smartbookfinder.entity.FavoriteBook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FavoriteBookRepository extends JpaRepository<FavoriteBook, Long> {
    Optional<FavoriteBook> findByBookKey(String bookKey);
    boolean existsByBookKey(String bookKey);
}
