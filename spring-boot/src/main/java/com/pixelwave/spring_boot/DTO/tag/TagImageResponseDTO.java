package com.pixelwave.spring_boot.DTO.tag;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TagImageResponseDTO {
    private Long imageId;
    private String url;
    private Long postId;
    private int likeCount;
} 