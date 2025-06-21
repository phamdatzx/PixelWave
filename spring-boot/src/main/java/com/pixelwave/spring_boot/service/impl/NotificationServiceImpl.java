package com.pixelwave.spring_boot.service.impl;

import com.pixelwave.spring_boot.DTO.notification.NotificationDTO;
import com.pixelwave.spring_boot.DTO.notification.WebSocketNotification;
import com.pixelwave.spring_boot.DTO.notification.WebSocketNotificationType;
import com.pixelwave.spring_boot.DTO.user.UserDTO;
import com.pixelwave.spring_boot.exception.ForbiddenException;
import com.pixelwave.spring_boot.exception.ResourceNotFoundException;
import com.pixelwave.spring_boot.model.Notification;
import com.pixelwave.spring_boot.model.NotificationType;
import com.pixelwave.spring_boot.model.User;
import com.pixelwave.spring_boot.repository.NotificationRepository;
import com.pixelwave.spring_boot.repository.UserRepository;
import com.pixelwave.spring_boot.service.NotificationService;
import com.pixelwave.spring_boot.service.WebSocketService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final SimpMessagingTemplate messagingTemplate;
    private final WebSocketService webSocketService;

    @Override
    @Transactional
    public void sendNotification(User recipient, User sender, NotificationType type, Long referenceId) {

        if(recipient.getId().equals(sender.getId())) return;

        Notification notification = Notification.builder()
                .recipient(recipient)
                .sender(sender)
                .isRead(false)
                .type(type.toString())
                .referenceId(referenceId)
                .content(generateContent(recipient, sender, type))
                .createdAt(LocalDateTime.now())
                .build();

        var notificationDTO = modelMapper.map(notificationRepository.save(notification), NotificationDTO.class);


//
//
        webSocketService.sendNotificationToUser(recipient.getId().toString(),notificationDTO);
    }

    @Transactional(readOnly = true)
    public Page<NotificationDTO> getUserNotifications(Long userId, Boolean isRead, Pageable pageable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        // Use Page<Notification> directly from repository to support efficient pagination
        Page<Notification> notificationPage;

        if (isRead != null) {
            notificationPage = notificationRepository.findByRecipientAndIsReadOrderByCreatedAtDesc(user, isRead, pageable);
        } else {
            notificationPage = notificationRepository.findByRecipientOrderByCreatedAtDesc(user, pageable);
        }

        // Map Notification entities to NotificationDTOs
        Page<NotificationDTO> dtoPage = notificationPage.map(notification -> NotificationDTO.builder()
                .id(notification.getId())
                .sender(modelMapper.map(notification.getSender(),UserDTO.class)) // Assuming you have a mapper or static method
                .type(NotificationType.valueOf(notification.getType()))
                .content(notification.getContent())
                .isRead(notification.isRead())
                .createdAt(notification.getCreatedAt())
                .referenceId(notification.getReferenceId())
                .build());

        return dtoPage;
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

    private String generateContent(User recipient, User sender, NotificationType type){
        return switch (type) {
            case NEW_POST -> sender.getFullName() + " has created a new post.";
            case NEW_COMMENT -> sender.getFullName() + " has commented on your post.";
            case NEW_FRIEND_REQUEST -> sender.getFullName() + " has sent you a friend request.";
            case FRIEND_REQUEST_ACCEPTED -> sender.getFullName() + " has accepted your friend request.";
            case REPLY_TO_COMMENT -> sender.getFullName() + " has replied to your comment.";
            case TAGGED_IN_POST -> sender.getFullName() + " has tagged you in a post.";
            default -> null;
        };
    }
} 