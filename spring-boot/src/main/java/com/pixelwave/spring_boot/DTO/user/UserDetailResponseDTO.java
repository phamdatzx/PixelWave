package com.pixelwave.spring_boot.DTO.user;

import com.pixelwave.spring_boot.model.Role;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDetailResponseDTO {
    private Long id;

    private String fullName;

    private String avatar;

    private String phoneNumber;

    private Integer age;

    private String gender;

    private String bio;

    private int postCount;

    private int followerCount;

    private int followingCount;

    private int friendCount;

}