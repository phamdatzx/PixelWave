package com.pixelwave.spring_boot.service.impl;

import com.pixelwave.spring_boot.DTO.notification.NotificationDTO;
import com.pixelwave.spring_boot.DTO.user.UserDTO;
import com.pixelwave.spring_boot.exception.ForbiddenException;
import com.pixelwave.spring_boot.exception.ResourceNotFoundException;
import com.pixelwave.spring_boot.model.Notification;
import com.pixelwave.spring_boot.model.NotificationType;
import com.pixelwave.spring_boot.model.User;
import com.pixelwave.spring_boot.repository.NotificationRepository;
import com.pixelwave.spring_boot.repository.UserRepository;
import com.pixelwave.spring_boot.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    @Transactional
    public void sendNotification(User recipient, User sender, NotificationType type, String content, Long referenceId) {
        Notification notification = Notification.builder()
                .recipient(recipient)
                .sender(sender)
                .type(type)
                .content(content)
                .referenceId(referenceId)
                .build();

        notificationRepository.save(notification);

        // Convert to DTO for WebSocket message
        NotificationDTO notificationDTO = convertToDTO(notification);

        // Send notification to specific user
        messagingTemplate.convertAndSendToUser(
                recipient.getUsername(),
                "/queue/notifications",
                notificationDTO
        );
    }

    @Override
    public List<NotificationDTO> getUserNotifications(UserDetails userDetails) {
        User user = (User) userDetails;
        return notificationRepository.findByRecipientOrderByCreatedAtDesc(user)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void markNotificationAsRead(Long notificationId, UserDetails userDetails) {
        User user = (User) userDetails;
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found"));

        if (!notification.getRecipient().getId().equals(user.getId())) {
            throw new ForbiddenException("You don't have permission to modify this notification");
        }

        notification.setRead(true);
        notificationRepository.save(notification);
    }

    @Override
    @Transactional
    public void markAllNotificationsAsRead(UserDetails userDetails) {
        User user = (User) userDetails;
        List<Notification> notifications = notificationRepository.findByRecipientAndIsReadFalse(user);
        notifications.forEach(notification -> notification.setRead(true));
        notificationRepository.saveAll(notifications);
    }

    @Override
    @Transactional
    public void deleteNotification(Long notificationId, UserDetails userDetails) {
        User user = (User) userDetails;
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found"));

        if (!notification.getRecipient().getId().equals(user.getId())) {
            throw new ForbiddenException("You don't have permission to delete this notification");
        }

        notificationRepository.delete(notification);
    }

    private NotificationDTO convertToDTO(Notification notification) {
        NotificationDTO dto = modelMapper.map(notification, NotificationDTO.class);
        dto.setSender(modelMapper.map(notification.getSender(), UserDTO.class));
        return dto;
    }
} 