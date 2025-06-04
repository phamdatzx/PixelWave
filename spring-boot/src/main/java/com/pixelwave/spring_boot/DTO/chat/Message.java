package com.pixelwave.spring_boot.DTO.chat;

import com.pixelwave.spring_boot.DTO.Image.ImageDTO;
import com.pixelwave.spring_boot.DTO.user.UserDTO;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class Message {
    private Long id;

    private String content;

    private UserDTO sender;

    LocalDateTime createdAt;

    private List<ImageDTO> images;
}
