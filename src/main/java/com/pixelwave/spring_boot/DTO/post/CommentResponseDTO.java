package com.pixelwave.spring_boot.DTO.post;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponseDTO {
    private Long id;
    private String content;
    private LocalDateTime createdAt;
    private UserDTO user;
    private List<ImageDTO> images;
    private boolean hasReplies;
}