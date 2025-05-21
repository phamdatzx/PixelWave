package com.pixelwave.spring_boot.DTO.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRecommendationDTO {
    private Long id;
    private String fullName;
    private String avatar;
    private int mutualFriendsCount;
} 