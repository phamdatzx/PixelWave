package com.pixelwave.spring_boot.DTO.report;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateReportDTO {
    @NotNull(message = "Post ID is required")
    private Long postId;

    @NotBlank(message = "Reason is required")
    private String reason;

    @NotBlank(message = "Description is required")
    private String description;
} 

// Inappropriate Content
// Harmful or Dangerous
// Personal Issues
// Other