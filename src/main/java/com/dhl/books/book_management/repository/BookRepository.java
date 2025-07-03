package com.dhl.books.book_management.repository;

import com.dhl.books.book_management.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    Optional<Book> findByIsbn(String isbn);
    
    @Query("SELECT b FROM Book b WHERE " +
           "(:title IS NULL OR LOWER(b.title) LIKE LOWER(CONCAT('%', :title, '%'))) AND " +
           "(:author IS NULL OR LOWER(b.author) LIKE LOWER(CONCAT('%', :author, '%'))) AND " +
           "(:isbn IS NULL OR b.isbn = :isbn) AND " +
           "(:categoryId IS NULL OR b.category.id = :categoryId) AND " +
           "(:language IS NULL OR b.language = :language) AND " +
           "(:fromDate IS NULL OR b.publicationDate >= :fromDate) AND " +
           "(:toDate IS NULL OR b.publicationDate <= :toDate) AND " +
           "(:availableOnly IS NULL OR :availableOnly = false OR b.availableQuantity > 0)")
    Page<Book> findBooksWithFilters(
        @Param("title") String title,
        @Param("author") String author,
        @Param("isbn") String isbn,
        @Param("categoryId") Long categoryId,
        @Param("language") String language,
        @Param("fromDate") LocalDate fromDate,
        @Param("toDate") LocalDate toDate,
        @Param("availableOnly") Boolean availableOnly,
        Pageable pageable
    );
    
    List<Book> findByAvailableQuantityGreaterThan(int quantity);
}