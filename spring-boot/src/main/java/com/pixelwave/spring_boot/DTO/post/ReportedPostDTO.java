package com.pixelwave.spring_boot.DTO.post;

import com.pixelwave.spring_boot.model.ReportStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportedPostDTO {
    private Long postId;
    private String caption;
    private String authorUsername;
    private LocalDateTime postCreatedAt;
    private int reportCount;
    private List<ReportDetailDTO> reports;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReportDetailDTO {
        private Long reportId;
        private String reporterUsername;
        private String reason;
        private String description;
        private ReportStatus status;
        private LocalDateTime reportedAt;
    }
} 