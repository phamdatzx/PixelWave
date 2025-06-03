package com.pixelwave.spring_boot.DTO.notification;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WebSocketNotification {
    private WebSocketNotificationType type;

    //for new message
    private String conversationId;

    private String content;
    //for new notification

    //for new friend request
}
