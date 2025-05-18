package com.pixelwave.spring_boot.DTO.post;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentRequestDTO {
    private String content;
    private Long postId;
    private Long parentCommentId; // Optional, for replies
} 