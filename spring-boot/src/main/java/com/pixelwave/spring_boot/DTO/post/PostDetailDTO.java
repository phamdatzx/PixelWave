package com.pixelwave.spring_boot.DTO.post;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.pixelwave.spring_boot.DTO.Image.ImageDTO;
import com.pixelwave.spring_boot.DTO.user.UserDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
public class PostDetailDTO {
    private Long id;
    private String caption;
    private Timestamp createdAt;
    private String privacySetting;
    private UserDTO postUser;
    private Integer likeCount;
    private Integer commentCount;
    private boolean isTaggedUser;
    private boolean isLiked;
    private Integer tagUserCount;
    private List<ImageDTO> images;  // Added images list
        
    public PostDetailDTO() {
        this.images = new ArrayList<>();
    }
}