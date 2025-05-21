package com.pixelwave.spring_boot.DTO.post;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PostSimpleDTO {
    private Long id;
    private String imageUrl;
    private int likeCount;
    private int commentCount;
}
