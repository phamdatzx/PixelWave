package com.pixelwave.spring_boot.config;

import com.pixelwave.spring_boot.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import java.security.Principal;

@Component
@RequiredArgsConstructor
public class ChannelSubscriptionInterceptor implements ChannelInterceptor {

    private final ChannelService channelService; // Add 'final' since using @RequiredArgsConstructor

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor != null && StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
            // Extract destination
            String destination = accessor.getDestination();
            if (destination != null && destination.startsWith("/topic/channel/")) {
                // Extract channel ID from destination
                String channelId = destination.replace("/topic/channel/", "");

                // Get user from auth object
                Principal principal = accessor.getUser();
                if (principal == null) {
                    throw new IllegalArgumentException("User not authenticated");
                }

                // Convert Principal to userid
                String userId;
                if (principal instanceof Authentication) {
                    userId = ((Authentication) principal).getName();
                } else {
                    userId = principal.getName();
                }

                // Check if user has access to this channel
                if (!channelService.canUserAccessChannel(userId, channelId)) {
                    throw new IllegalArgumentException("No permission to access this channel");
                }
            }
        }

        return message;
    }
}