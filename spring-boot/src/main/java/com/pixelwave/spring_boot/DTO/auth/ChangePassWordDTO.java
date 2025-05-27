package com.pixelwave.spring_boot.DTO.auth;

import lombok.Data;

@Data
public class ChangePassWordDTO {
    String oldPassword;
    String newPassword;
}
