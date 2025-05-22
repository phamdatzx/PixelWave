package com.pixelwave.spring_boot.service;

import com.pixelwave.spring_boot.DTO.notification.NotificationDTO;
import com.pixelwave.spring_boot.model.NotificationType;
import com.pixelwave.spring_boot.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

public interface NotificationService {
    void sendNotification(User recipient, User sender, NotificationType type, Long referenceId);
    Page<NotificationDTO> getUserNotifications(Long userId, Boolean isRead, Pageable pageable);
    void markNotificationAsRead(Long notificationId, UserDetails userDetails);
    void markAllNotificationsAsRead(UserDetails userDetails);
    void deleteNotification(Long notificationId, UserDetails userDetails);
} 