package com.pixelwave.spring_boot.DTO.user;

import com.pixelwave.spring_boot.model.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponseDTO {
    private Long id;

    private String fullName;

    private String avatar;

    private Role role;
}
