package com.pixelwave.spring_boot.controller;

import com.pixelwave.spring_boot.DTO.ChannelSubscription;
import com.pixelwave.spring_boot.DTO.WebSocketMessageDTO;
import com.pixelwave.spring_boot.model.Message;
import com.pixelwave.spring_boot.repository.MessageRepository;
import com.pixelwave.spring_boot.service.ChannelManager;
import com.pixelwave.spring_boot.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class WebSocketController {

    private final ChatService chatService;

    private final  SimpMessagingTemplate messagingTemplate;

    private final ChannelManager channelManager;

    /**
     * Handle messages sent to a specific channel
     */
    @MessageMapping("/conversation/{conversationId}")
    public void sendMessage(@DestinationVariable String conversationId, @Payload WebSocketMessageDTO message) {
        message.setChannelId(conversationId);
        //save to database
        var savedMessage = chatService.createMessage(conversationId, message.getContent(), Long.parseLong(message.getSender()));

        message.setId(savedMessage.getId());

        // Send message to channel subscribers
        messagingTemplate.convertAndSend("/topic/conversation/" + conversationId, message);
    }

    /**
     * Handle channel subscription requests
     */
    @MessageMapping("/conversation/subscribe")
    public void subscribeChannel(@Payload ChannelSubscription subscription,
                                 SimpMessageHeaderAccessor headerAccessor) {
        String userId = subscription.getUserId();
        String channelId = subscription.getChannelId();

        // Subscribe user to channel
        channelManager.subscribe(channelId, userId);

        // Store user in WebSocket session for handling disconnect events
        headerAccessor.getSessionAttributes().put("userId", userId);
        headerAccessor.getSessionAttributes().put("conversationId", channelId);

        // Notify channel about new user
        WebSocketMessageDTO message = new WebSocketMessageDTO();
        message.setType(WebSocketMessageDTO.MessageType.JOIN);
        message.setSender(userId);
        message.setChannelId(channelId);
        message.setContent(userId + " joined the conversation");

        messagingTemplate.convertAndSend("/topic/conversation/" + channelId, message);
    }

    /**
     * Handle channel unsubscription requests
     */
    @MessageMapping("/conversation/unsubscribe")
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

        messagingTemplate.convertAndSend("/topic/conversation/" + channelId, message);
    }
}