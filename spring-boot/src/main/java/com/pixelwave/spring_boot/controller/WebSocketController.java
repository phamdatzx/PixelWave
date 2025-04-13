package com.pixelwave.spring_boot.controller;

import com.pixelwave.spring_boot.DTO.ChannelSubscription;
import com.pixelwave.spring_boot.DTO.WebSocketMessageDTO;
import com.pixelwave.spring_boot.service.ChannelManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private ChannelManager channelManager;

    /**
     * Handle messages sent to a specific channel
     */
    @MessageMapping("/channel/{channelId}/send")
    public void sendMessage(@DestinationVariable String channelId, @Payload WebSocketMessageDTO message) {
        message.setChannelId(channelId);

        // Check if channel exists, if not create it
        if (!channelManager.channelExists(channelId)) {
            channelManager.createChannel(channelId);
        }

        // Send message to channel subscribers
        messagingTemplate.convertAndSend("/topic/channel/" + channelId, message);
    }

    /**
     * Handle channel subscription requests
     */
    @MessageMapping("/channel/subscribe")
    public void subscribeChannel(@Payload ChannelSubscription subscription,
                                 SimpMessageHeaderAccessor headerAccessor) {
        String userId = subscription.getUserId();
        String channelId = subscription.getChannelId();

        // Subscribe user to channel
        channelManager.subscribe(channelId, userId);

        // Store user in WebSocket session for handling disconnect events
        headerAccessor.getSessionAttributes().put("userId", userId);
        headerAccessor.getSessionAttributes().put("channelId", channelId);

        // Notify channel about new user
        WebSocketMessageDTO message = new WebSocketMessageDTO();
        message.setType(WebSocketMessageDTO.MessageType.JOIN);
        message.setSender(userId);
        message.setChannelId(channelId);
        message.setContent(userId + " joined the channel");

        messagingTemplate.convertAndSend("/topic/channel/" + channelId, message);
    }

    /**
     * Handle channel unsubscription requests
     */
    @MessageMapping("/channel/unsubscribe")
    public void unsubscribeChannel(@Payload ChannelSubscription subscription) {
        String userId = subscription.getUserId();
        String channelId = subscription.getChannelId();

        // Unsubscribe user from channel
        channelManager.unsubscribe(channelId, userId);

        // Notify channel about user leaving
        WebSocketMessageDTO message = new WebSocketMessageDTO();
        message.setType(WebSocketMessageDTO.MessageType.LEAVE);
        message.setSender(userId);
        message.setChannelId(channelId);
        message.setContent(userId + " left the channel");

        messagingTemplate.convertAndSend("/topic/channel/" + channelId, message);
    }
}