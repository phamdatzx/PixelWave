package com.pixelwave.spring_boot.service;

import com.pixelwave.spring_boot.DTO.notification.NotificationDTO;
import com.pixelwave.spring_boot.model.NotificationType;
import com.pixelwave.spring_boot.model.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

public interface NotificationService {
    void sendNotification(User recipient, User sender, NotificationType type, String content, Long referenceId);
    List<NotificationDTO> getUserNotifications(UserDetails userDetails);
    void markNotificationAsRead(Long notificationId, UserDetails userDetails);
    void markAllNotificationsAsRead(UserDetails userDetails);
    void deleteNotification(Long notificationId, UserDetails userDetails);
} 