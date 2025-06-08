package com.pixelwave.spring_boot.service;

import com.pixelwave.spring_boot.DTO.report.CreateReportDTO;
import com.pixelwave.spring_boot.DTO.report.ReportDTO;
import com.pixelwave.spring_boot.DTO.user.UserDTO;
import com.pixelwave.spring_boot.DTO.user.UserViolationCountDTO;
import com.pixelwave.spring_boot.DTO.violation.ViolationDTO;
import com.pixelwave.spring_boot.model.ReportStatus;
import com.pixelwave.spring_boot.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ReportService {
    ReportDTO createReport(CreateReportDTO createReportDTO, User reporter);
    Page<ReportDTO> getReports(ReportStatus status, Pageable pageable);
    ReportDTO updateReportStatus(Long reportId, ReportStatus newStatus);
    void deleteReportedPost(Long postId);
    List<UserViolationCountDTO> getUserViolationCounts();
    List<ViolationDTO> getUserViolations(Long userId);
    UserDTO banUser(Long userId);
} 