package com.pixelwave.spring_boot.DTO.notification;

import com.pixelwave.spring_boot.DTO.user.UserDTO;
import com.pixelwave.spring_boot.model.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDTO {
    private Long id;
    private UserDTO sender;
    private NotificationType type;
    private String content;
    private boolean isRead;
    private LocalDateTime createdAt;
    private Long referenceId;
} 