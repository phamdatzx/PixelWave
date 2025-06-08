package com.pixelwave.spring_boot.controller;

import com.pixelwave.spring_boot.DTO.report.CreateReportDTO;
import com.pixelwave.spring_boot.DTO.report.ReportDTO;
import com.pixelwave.spring_boot.model.ReportStatus;
import com.pixelwave.spring_boot.model.User;
import com.pixelwave.spring_boot.service.ReportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @PostMapping
    public ResponseEntity<ReportDTO> createReport(
            @Valid @RequestBody CreateReportDTO createReportDTO,
            @AuthenticationPrincipal User reporter) {
        return ResponseEntity.ok(reportService.createReport(createReportDTO, reporter));
    }

    @GetMapping
    public ResponseEntity<Page<ReportDTO>> getReports(
            @RequestParam(required = false) ReportStatus status,
            @PageableDefault(sort = "createdAt", direction = org.springframework.data.domain.Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(reportService.getReports(status, pageable));
    }

    @PatchMapping("/{reportId}/status")
    public ResponseEntity<ReportDTO> updateReportStatus(
            @PathVariable Long reportId,
            @RequestParam ReportStatus status) {
        return ResponseEntity.ok(reportService.updateReportStatus(reportId, status));
    }

    @DeleteMapping("/posts/{postId}")
    public ResponseEntity<Void> deleteReportedPost(@PathVariable Long postId) {
        reportService.deleteReportedPost(postId);
        return ResponseEntity.noContent().build();
    }
} 