package com.pixelwave.spring_boot.DTO.violation;

import com.pixelwave.spring_boot.DTO.user.UserDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ViolationDTO {
    private Long id;
    private UserDTO reporter;
    private String reason;
    private String description;
    private LocalDateTime createdAt;
} 