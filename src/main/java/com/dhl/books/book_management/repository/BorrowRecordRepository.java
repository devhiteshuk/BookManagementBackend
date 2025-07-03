package com.dhl.books.book_management.repository;

import com.dhl.books.book_management.entity.BorrowRecord;
import com.dhl.books.book_management.entity.BorrowStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BorrowRecordRepository extends JpaRepository<BorrowRecord, Long> {
    List<BorrowRecord> findByUserId(Long userId);

    List<BorrowRecord> findByBookId(Long bookId);

    List<BorrowRecord> findByStatus(BorrowStatus status);

    Page<BorrowRecord> findByUserId(Long userId, Pageable pageable);

    @Query("SELECT br FROM BorrowRecord br WHERE br.dueDate < :currentDate AND br.status = :status")
    List<BorrowRecord> findOverdueRecords(@Param("currentDate") LocalDateTime currentDate, @Param("status") BorrowStatus status);

    @Query("SELECT COUNT(br) FROM BorrowRecord br WHERE br.user.id = :userId AND br.status IN :statuses")
    int countActiveRecordsByUserId(@Param("userId") Long userId, @Param("statuses") List<BorrowStatus> statuses);
}