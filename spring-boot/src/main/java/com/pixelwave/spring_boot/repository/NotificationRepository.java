package com.pixelwave.spring_boot.repository;

import com.pixelwave.spring_boot.model.Notification;
import com.pixelwave.spring_boot.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    Page<Notification> findByRecipientOrderByCreatedAtDesc(User recipient, Pageable pageable);
    Page<Notification> findByRecipientAndIsReadOrderByCreatedAtDesc(User user, Boolean isRead, Pageable pageable);

    List<Notification> findByRecipientAndIsReadFalse(User user);
}