package com.dhl.books.book_management.service;

import com.dhl.books.book_management.dto.BookDTO;
import com.dhl.books.book_management.entity.Book;
import com.dhl.books.book_management.entity.Category;
import com.dhl.books.book_management.repository.BookRepository;
import com.dhl.books.book_management.repository.CategoryRepository;
import com.dhl.books.book_management.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional
public class BookService {
    
    @Autowired
    private BookRepository bookRepository;
    
    @Autowired
    private CategoryRepository categoryRepository;
    
    @Autowired
    private ReviewRepository reviewRepository;
    
    @Autowired
    private ActivityLogService activityLogService;
    
    public Page<BookDTO> getAllBooks(Pageable pageable) {
        return bookRepository.findAll(pageable).map(this::convertToDTO);
    }
    
    public Page<BookDTO> searchBooks(String title, String author, String isbn, 
                                   Long categoryId, String language, 
                                   LocalDate fromDate, LocalDate toDate, 
                                   Boolean availableOnly, Pageable pageable) {
        return bookRepository.findBooksWithFilters(title, author, isbn, categoryId, 
            language, fromDate, toDate, availableOnly, pageable).map(this::convertToDTO);
    }
    
    public Optional<BookDTO> getBookById(Long id) {
        return bookRepository.findById(id).map(this::convertToDTO);
    }
    
    public BookDTO createBook(BookDTO bookDTO) {
        Book book = new Book();
        book.setTitle(bookDTO.getTitle());
        book.setAuthor(bookDTO.getAuthor());
        book.setIsbn(bookDTO.getIsbn());
        book.setPublisher(bookDTO.getPublisher());
        book.setPublicationDate(bookDTO.getPublicationDate());
        book.setLanguage(bookDTO.getLanguage());
        book.setDescription(bookDTO.getDescription());
        book.setQuantity(bookDTO.getQuantity());
        book.setAvailableQuantity(bookDTO.getQuantity());
        book.setCoverImageUrl(bookDTO.getCoverImageUrl());
        book.setCreatedAt(LocalDateTime.now());
        book.setUpdatedAt(LocalDateTime.now());
        
        if (bookDTO.getCategoryId() != null) {
            Category category = categoryRepository.findById(bookDTO.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));
            book.setCategory(category);
        }
        
        Book savedBook = bookRepository.save(book);
        activityLogService.logActivity(null, "CREATE_BOOK", "Book", savedBook.getId(), 
            "Book created: " + savedBook.getTitle(), "system");
        
        return convertToDTO(savedBook);
    }
    
    public BookDTO updateBook(Long id, BookDTO bookDTO) {
        Book book = bookRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Book not found"));
        
        book.setTitle(bookDTO.getTitle());
        book.setAuthor(bookDTO.getAuthor());
        book.setIsbn(bookDTO.getIsbn());
        book.setPublisher(bookDTO.getPublisher());
        book.setPublicationDate(bookDTO.getPublicationDate());
        book.setLanguage(bookDTO.getLanguage());
        book.setDescription(bookDTO.getDescription());
        book.setCoverImageUrl(bookDTO.getCoverImageUrl());
        book.setUpdatedAt(LocalDateTime.now());
        
        // Update quantity and available quantity
        int quantityDiff = bookDTO.getQuantity() - book.getQuantity();
        book.setQuantity(bookDTO.getQuantity());
        book.setAvailableQuantity(book.getAvailableQuantity() + quantityDiff);
        
        if (bookDTO.getCategoryId() != null) {
            Category category = categoryRepository.findById(bookDTO.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));
            book.setCategory(category);
        }
        
        Book updatedBook = bookRepository.save(book);
        return convertToDTO(updatedBook);
    }
    
    public void deleteBook(Long id) {
        Book book = bookRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Book not found"));
        
        bookRepository.delete(book);
        activityLogService.logActivity(null, "DELETE_BOOK", "Book", id, 
            "Book deleted: " + book.getTitle(), "system");
    }
    
    private BookDTO convertToDTO(Book book) {
        BookDTO dto = new BookDTO();
        dto.setId(book.getId());
        dto.setTitle(book.getTitle());
        dto.setAuthor(book.getAuthor());
        dto.setIsbn(book.getIsbn());
        dto.setPublisher(book.getPublisher());
        dto.setPublicationDate(book.getPublicationDate());
        dto.setLanguage(book.getLanguage());
        dto.setDescription(book.getDescription());
        dto.setQuantity(book.getQuantity());
        dto.setAvailableQuantity(book.getAvailableQuantity());
        dto.setCoverImageUrl(book.getCoverImageUrl());
        
        if (book.getCategory() != null) {
            dto.setCategoryId(book.getCategory().getId());
            dto.setCategoryName(book.getCategory().getName());
        }
        
        // Calculate average rating
        Double avgRating = reviewRepository.findAverageRatingByBookId(book.getId());
        dto.setAverageRating(avgRating);
        
        return dto;
    }
}