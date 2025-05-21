package com.pixelwave.spring_boot.DTO.post;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CommentRequestDTO {
    private String content;
    private Long postId;
    private Long parentCommentId; // Optional, for replies
    private List<MultipartFile> images;

} 