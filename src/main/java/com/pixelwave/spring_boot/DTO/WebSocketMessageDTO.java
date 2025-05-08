package com.pixelwave.spring_boot.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WebSocketMessageDTO {
    private String content;
    private String sender;
    private String channelId;
    private MessageType type;
    private long timestamp = System.currentTimeMillis();

    public enum MessageType {
        CHAT, JOIN, LEAVE
    }
}
