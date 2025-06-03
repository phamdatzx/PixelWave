package com.pixelwave.spring_boot.service;

import com.pixelwave.spring_boot.DTO.notification.NotificationDTO;
import com.pixelwave.spring_boot.config.ChannelSubscriptionInterceptor;
import com.pixelwave.spring_boot.config.WebsocketEventListener;
import com.pixelwave.spring_boot.model.NotificationType;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class WebSocketService {
    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    private UserSessionManager userSessionManager; // Inject UserSessionManager instead

    /**
     * Send message to a specific user by their user ID
     */
    public boolean sendMessageToUser(String userId, String destination, Object message) {
        if (userSessionManager.isUserConnected(userId)) {
            simpMessagingTemplate.convertAndSendToUser(userId, destination, message);
            return true;
        }
        return false;
    }

    /**
     * Send notification to a specific user
     */
    public boolean sendNotificationToUser(String userId, String notification) {

        NotificationDTO notificationDTO = NotificationDTO.builder()
                .id(null) // ID can be set later if needed
                .content("test")
                .type(NotificationType.NEW_POST)
                .build();

        return sendMessageToUser(userId, "/queue/notifications", notificationDTO);
    }

    /**
     * Check if user is currently connected
     */
    public boolean isUserOnline(String userId) {
        return userSessionManager.isUserConnected(userId);
    }

    /**
     * Get all connected users
     */
    public Map<String, String> getConnectedUsers() {
        return userSessionManager.getAllConnectedUsers();
    }

    /**
     * Send message to all users subscribed to a topic
     */
    public void sendToTopic(String topic, Object message) {
        simpMessagingTemplate.convertAndSend("/topic/" + topic, message);
    }

    /**
     * Send message to channel
     */
    public void sendToChannel(String channelId, Object message) {
        simpMessagingTemplate.convertAndSend("/topic/channel/" + channelId, message);
    }

    /**
     * Get count of connected users
     */
    public int getConnectedUserCount() {
        return userSessionManager.getConnectedUserCount();
    }
}
