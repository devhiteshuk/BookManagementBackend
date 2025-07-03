package com.dhl.books.book_management.service;

import com.dhl.books.book_management.entity.ActivityLog;
import com.dhl.books.book_management.entity.User;
import com.dhl.books.book_management.repository.ActivityLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ActivityLogService {
    
    @Autowired
    private ActivityLogRepository activityLogRepository;
    
    public void logActivity(User user, String action, String entityType, Long entityId,
                            String details, String ipAddress) {
        ActivityLog log = new ActivityLog(user, action, entityType, entityId, details, ipAddress);
        activityLogRepository.save(log);
    }
    
    public Page<ActivityLog> getAllLogs(Pageable pageable) {
        return activityLogRepository.findAll(pageable);
    }
    
    public Page<ActivityLog> getLogsByUserId(Long userId, Pageable pageable) {
        return activityLogRepository.findByUserId(userId, pageable);
    }
}