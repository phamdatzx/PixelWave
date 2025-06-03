package com.pixelwave.spring_boot.config;

import com.pixelwave.spring_boot.model.User;
import com.pixelwave.spring_boot.service.ChannelService;
import com.pixelwave.spring_boot.service.ConversationService;
import com.pixelwave.spring_boot.service.JwtService;
import com.pixelwave.spring_boot.service.UserSessionManager;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class ChannelSubscriptionInterceptor implements ChannelInterceptor {

    private final ChannelService channelService;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final ConversationService conversationService;
    private final UserSessionManager userSessionManager;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor != null) {
            if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                return handleConnect(accessor, message);
            } else if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
                return handleSubscribe(accessor, message);
            } else if (StompCommand.DISCONNECT.equals(accessor.getCommand())) {
                return handleDisconnect(accessor, message);
            }
        }

        return message;
    }

    private Message<?> handleConnect(StompHeaderAccessor accessor, Message<?> message) {
        String sessionId = accessor.getSessionId();
        List<String> authHeaders = accessor.getNativeHeader("Authorization");

        if (authHeaders != null && !authHeaders.isEmpty()) {
            String token = authHeaders.get(0);
            System.out.println("CONNECT - Token received: " + token);

            try {
                String userId = validateTokenAndGetUserId(token);

                if (userId != null) {
                    userSessionManager.addUserSession(userId, sessionId);

                    // Store in session attributes
                    accessor.getSessionAttributes().put("userId", userId);
                    accessor.getSessionAttributes().put("accessToken", token);
                    accessor.getSessionAttributes().put("authenticated", true);

                    // IMPORTANT: Set the user principal for Spring's user-specific messaging
                    // This is crucial for convertAndSendToUser to work properly
                    accessor.setUser(() -> userId); // Set user principal to userId

                    System.out.println("✓ User authenticated and principal set: " + userId);
                    System.out.println("✓ Session ID: " + sessionId);

                    return message;
                } else {
                    throw new SecurityException("Invalid authentication token");
                }

            } catch (Exception e) {
                System.err.println("Authentication failed: " + e.getMessage());
                throw new SecurityException("Authentication failed: " + e.getMessage());
            }
        } else {
            throw new SecurityException("Missing authentication token");
        }
    }

    private Message<?> handleSubscribe(StompHeaderAccessor accessor, Message<?> message) {
        String sessionId = accessor.getSessionId();
        String destination = accessor.getDestination();
        String userId = (String) accessor.getSessionAttributes().get("userId");

        System.out.println("=== SUBSCRIBE Debug ===");
        System.out.println("User: " + userId);
        System.out.println("Session: " + sessionId);
        System.out.println("Destination: " + destination);
        System.out.println("User Principal: " + (accessor.getUser() != null ? accessor.getUser().getName() : "null"));

        if (userId == null) {
            throw new SecurityException("User not authenticated");
        }

        // Validate conversation subscriptions
        if (destination != null && destination.startsWith("/topic/conversation/")) {
            String conversationId = destination.replace("/topic/conversation/", "");
            if (!conversationService.isUserIdInConversation(Long.parseLong(userId), conversationId)) {
                throw new SecurityException("User does not have permission to access this channel");
            }
        }

        // Log user queue subscriptions for debugging
        if (destination != null && destination.startsWith("/user/queue/")) {
            System.out.println("✓ User subscribing to personal queue: " + destination);
        }

        return message;
    }

    private Message<?> handleDisconnect(StompHeaderAccessor accessor, Message<?> message) {
        String sessionId = accessor.getSessionId();
        String userId = userSessionManager.getUserIdBySessionId(sessionId);

        if (userId != null) {
            userSessionManager.removeUserSession(userId);
            System.out.println("✓ User disconnected: " + userId);
        }

        return message;
    }

    private String validateTokenAndGetUserId(String bearerToken) {
        try {
            String token = bearerToken.startsWith("Bearer ") ?
                    bearerToken.substring(7) : bearerToken;

            if (jwtService.isTokenValid(token)) {
                var user = userDetailsService.loadUserByUsername(jwtService.extractSubject(token));
                return ((User) user).getId().toString();
            }

            return null;
        } catch (Exception e) {
            System.err.println("Token validation error: " + e.getMessage());
            return null;
        }
    }
}