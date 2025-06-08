package com.pixelwave.spring_boot.DTO.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserViolationCountDTO {
    private UserDTO user;
    private Long violationCount;
} 