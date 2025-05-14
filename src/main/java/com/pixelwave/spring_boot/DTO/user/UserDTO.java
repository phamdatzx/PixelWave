package com.pixelwave.spring_boot.DTO.user;

import lombok.Data;

@Data
public class UserDTO {
    private Long id;
    private String fullName;
    private String avatar;
}