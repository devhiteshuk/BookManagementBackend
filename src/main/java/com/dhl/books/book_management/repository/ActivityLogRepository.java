package com.dhl.books.book_management.repository;

import com.dhl.books.book_management.entity.ActivityLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {
    List<ActivityLog> findByUserId(Long userId);
    Page<ActivityLog> findByUserId(Long userId, Pageable pageable);
    List<ActivityLog> findByTimestampBetween(LocalDateTime startDate, LocalDateTime endDate);
    List<ActivityLog> findByAction(String action);
}