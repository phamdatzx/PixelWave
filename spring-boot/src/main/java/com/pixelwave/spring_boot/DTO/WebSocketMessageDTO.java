package com.pixelwave.spring_boot.DTO;

import com.pixelwave.spring_boot.DTO.Image.ImageDTO;
import com.pixelwave.spring_boot.model.Image;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WebSocketMessageDTO {
    private Long id;
    private String content;
    private String sender;
    private String channelId;
    private MessageType type;
    private List<ImageDTO> images;
    private long timestamp = System.currentTimeMillis();

    public enum MessageType {
        CHAT, JOIN, LEAVE
    }
}
