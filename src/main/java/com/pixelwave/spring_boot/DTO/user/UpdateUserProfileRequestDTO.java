package com.pixelwave.spring_boot.DTO.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserProfileRequestDTO {
    private String phoneNumber;

    private Integer age;

    private String gender;

    private String bio;
}
