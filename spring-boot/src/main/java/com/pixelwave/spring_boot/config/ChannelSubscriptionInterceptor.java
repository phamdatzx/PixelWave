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
    private final JwtService jwtService; // Inject your JWT service
    private final UserDetailsService userDetailsService; // Inject UserDetailsService if needed
    private final ConversationService conversationService; // Inject ConversationService if needed
    // Store user sessions (same as your event listener)

    private final UserSessionManager userSessionManager;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor != null) {
            String sessionId = accessor.getSessionId();

            // Handle CONNECT command (first STOMP frame after WebSocket connection)
            if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                return handleConnect(accessor, message);
            }

            // Handle SUBSCRIBE command
            else if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
                return handleSubscribe(accessor, message);
            }
//
//            // Handle DISCONNECT command
            else if (StompCommand.DISCONNECT.equals(accessor.getCommand())) {
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
                    // Use UserSessionManager instead of local maps
                    userSessionManager.addUserSession(userId, sessionId);

                    // Store in session attributes
                    accessor.getSessionAttributes().put("userId", userId);
                    accessor.getSessionAttributes().put("accessToken", token);
                    accessor.getSessionAttributes().put("authenticated", true);

                    System.out.println("User authenticated: " + userId);
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

        // Get user ID from session attributes (set during CONNECT)
        String userId = (String) accessor.getSessionAttributes().get("userId");
        Boolean isAuthenticated = (Boolean) accessor.getSessionAttributes().get("authenticated");

        System.out.println("SUBSCRIBE - User: " + userId + ", Destination: " + destination);

        if (userId == null || !Boolean.TRUE.equals(isAuthenticated)) {
            System.out.println("Unauthenticated user trying to subscribe");
            throw new SecurityException("User not authenticated");
        }

        if (destination != null && destination.startsWith("/topic/conversation/")) {
            // Extract channel ID from destination
            String conversationId = destination.replace("/topic/conversation/", "");

            System.out.println("User " + userId + " subscribing to channel: " + conversationId);

            // Check if user has permission to access this channel
            if(!conversationService.isUserIdInConversation(Long.parseLong(userId), conversationId)){
                System.out.println("User " + userId + " does not have permission to subscribe to channel: " + conversationId);
                throw new SecurityException("User does not have permission to access this channel");
            }

            System.out.println("User " + userId + " successfully subscribed to channel: " + conversationId);
        }

        return message;
    }
//
    private Message<?> handleDisconnect(StompHeaderAccessor accessor, Message<?> message) {
        String sessionId = accessor.getSessionId();
        String userId = userSessionManager.getUserIdBySessionId(sessionId);

        if (userId != null) {
            userSessionManager.removeUserSession(userId);
        }

        return message;
    }

    private String validateTokenAndGetUserId(String bearerToken) {
        try {
            // Remove "Bearer " prefix if present
            String token = bearerToken.startsWith("Bearer ") ?
                    bearerToken.substring(7) : bearerToken;

            // Validate token using your JWT service
            if (jwtService.isTokenValid(token)) {
                var user = userDetailsService.loadUserByUsername(jwtService.extractSubject(token)); // or extractUserId(token)
                return ((User)user).getId().toString();
            }

            return null;
        } catch (Exception e) {
            System.err.println("Token validation error: " + e.getMessage());
            return null;
        }
    }
}