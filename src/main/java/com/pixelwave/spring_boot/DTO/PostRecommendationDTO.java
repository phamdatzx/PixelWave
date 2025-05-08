package com.pixelwave.spring_boot.DTO;

import lombok.Data;
import java.sql.Timestamp;

public interface PostRecommendationDTO {
    Long getId();
    String getCaption();
    String getPrivacySetting();
    Timestamp getCreatedAt();
    Integer getCommentCount();
    Integer getLikeCount();
    Long getUserId();
    String getAvatar();
    String getFullName();
    Integer getUserViewCount();
}
