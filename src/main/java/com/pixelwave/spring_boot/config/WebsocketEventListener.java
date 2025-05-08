package com.pixelwave.spring_boot.config;

import com.pixelwave.spring_boot.DTO.WebSocketMessageDTO;
import com.pixelwave.spring_boot.service.ChannelManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
public class WebsocketEventListener {
    private static final Logger logger = LoggerFactory.getLogger(WebsocketEventListener.class);

    @Autowired
    private SimpMessageSendingOperations messagingTemplate;

    @Autowired
    private ChannelManager channelManager;

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        logger.info("Received a new web socket connection");
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

        String userId = (String) headerAccessor.getSessionAttributes().get("userId");
        String channelId = (String) headerAccessor.getSessionAttributes().get("channelId");

        if(userId != null && channelId != null) {
            logger.info("User Disconnected : " + userId);

            WebSocketMessageDTO message = new WebSocketMessageDTO();
            message.setType(WebSocketMessageDTO.MessageType.LEAVE);
            message.setSender(userId);
            message.setChannelId(channelId);
            message.setContent(userId + " left the channel");

            channelManager.unsubscribe(channelId, userId);
            messagingTemplate.convertAndSend("/topic/channel/" + channelId, message);
        }
    }
}
