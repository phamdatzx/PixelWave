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

    public boolean sendMessageToUser(String userId, String destination, Object message) {
        System.out.println("=== Attempting to send message ===");
        System.out.println("User ID: " + userId);
        System.out.println("Destination: " + destination);
        System.out.println("Message: " + message);
        System.out.println("Is Connected: " + userSessionManager.isUserConnected(userId));

        // Print all sessions for debugging
        userSessionManager.printAllSessions();

        if (userSessionManager.isUserConnected(userId)) {
            try {
                // Method 1: Using convertAndSendToUser with userId
                simpMessagingTemplate.convertAndSend("/user/queue/notifications/" + userId, message);


                System.out.println("✓ Message sent using convertAndSendToUser with userId: " + userId);

                // Method 2: Alternative - send directly to session (uncomment to try)
//                 String sessionId = userSessionManager.getSessionIdByUserId(userId);
//                 simpMessagingTemplate.convertAndSendToUser(sessionId, destination, message);
//                 System.out.println("✓ Message sent using convertAndSendToUser with sessionId: " + sessionId);

                return true;
            } catch (Exception e) {
                System.err.println("✗ Error sending message: " + e.getMessage());
                e.printStackTrace();
                return false;
            }
        }

        System.out.println("✗ User " + userId + " not connected");
        return false;
    }

    /**
     * Send notification to a specific user
     */
    public boolean sendNotificationToUser(String userId, NotificationDTO notification) {


        return sendMessageToUser(userId, "/user/queue/notifications/", notification);
    }

    // Alternative method using sessionId directly
    public boolean sendNotificationToUserBySession(String userId, String notification) {
        String sessionId = userSessionManager.getSessionIdByUserId(userId);
        if (sessionId == null) {
            System.out.println("No session found for user: " + userId);
            return false;
        }

        NotificationDTO notificationDTO = NotificationDTO.builder()
                .id(null)
                .content(notification)
                .type(NotificationType.NEW_POST)
                .build();

        try {
            // Send using sessionId as the user identifier
            simpMessagingTemplate.convertAndSendToUser(sessionId, "/user/queue/notifications/", notificationDTO);
            System.out.println("✓ Notification sent to session: " + sessionId + " for user: " + userId);
            return true;
        } catch (Exception e) {
            System.err.println("✗ Error sending notification: " + e.getMessage());
            return false;
        }
    }

    /**
     * Check if user is currently connected
     */
    public boolean isUserOnline(String userId) {
        return userSessionManager.isUserConnected(userId);
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
}
