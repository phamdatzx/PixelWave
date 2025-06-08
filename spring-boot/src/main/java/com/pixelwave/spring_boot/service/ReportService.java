package com.pixelwave.spring_boot.service;

import com.pixelwave.spring_boot.DTO.report.CreateReportDTO;
import com.pixelwave.spring_boot.DTO.report.ReportDTO;
import com.pixelwave.spring_boot.model.ReportStatus;
import com.pixelwave.spring_boot.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReportService {
    ReportDTO createReport(CreateReportDTO createReportDTO, User reporter);
    Page<ReportDTO> getReports(ReportStatus status, Pageable pageable);
    ReportDTO updateReportStatus(Long reportId, ReportStatus newStatus);
    void deleteReportedPost(Long postId);
} 