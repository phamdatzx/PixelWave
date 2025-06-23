package com.pixelwave.spring_boot.DTO.report;

import com.pixelwave.spring_boot.DTO.user.UserDTO;
import com.pixelwave.spring_boot.model.ReportStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportDTO {
    private Long id;
    private Long postId;
    private Long reporterId;
    private String reason;
    private String description;
    private ReportStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 