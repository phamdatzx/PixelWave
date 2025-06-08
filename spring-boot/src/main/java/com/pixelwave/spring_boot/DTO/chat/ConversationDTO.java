package com.pixelwave.spring_boot.DTO.chat;

import com.pixelwave.spring_boot.DTO.user.UserDTO;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ConversationDTO {
    private String id;
    private UserDTO user;
    private LocalDateTime lastUpdated;
    private String lastMessageContent;
}
