package com.pixelwave.spring_boot.DTO.post;

import com.pixelwave.spring_boot.DTO.Image.ImageDTO;
import com.pixelwave.spring_boot.DTO.user.UserResponseDTO;
import com.pixelwave.spring_boot.model.Image;
import com.pixelwave.spring_boot.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostResponseDTO {

    private Long id;

    private String caption;

    private String privacySetting;

    private LocalDateTime createdAt;

    private UserResponseDTO user;

    private Set<ImageDTO> images = new HashSet<>();

    private Set<UserResponseDTO> taggedUsers = new HashSet<>();

    private int likeCount;

    private int commentCount;

    private boolean isLikedByUser;
}
