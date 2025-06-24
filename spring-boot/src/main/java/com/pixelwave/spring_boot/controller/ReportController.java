package com.pixelwave.spring_boot.controller;

import com.pixelwave.spring_boot.DTO.post.ReportedPostDTO;
import com.pixelwave.spring_boot.DTO.report.CreateReportDTO;
import com.pixelwave.spring_boot.DTO.report.ReportDTO;
import com.pixelwave.spring_boot.DTO.user.UserDTO;
import com.pixelwave.spring_boot.DTO.user.UserViolationCountDTO;
import com.pixelwave.spring_boot.DTO.violation.ViolationDTO;
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

import java.util.List;

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
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<ReportDTO>> getReports(
            @RequestParam(required = false) ReportStatus status,
            @PageableDefault(sort = "createdAt", direction = org.springframework.data.domain.Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(reportService.getReports(status, pageable));
    }

    @PatchMapping("/{reportId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ReportDTO> updateReportStatus(
            @PathVariable Long reportId,
            @RequestParam ReportStatus status) {
        return ResponseEntity.ok(reportService.updateReportStatus(reportId, status));
    }

    @DeleteMapping("/posts/{postId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteReportedPost(@PathVariable Long postId) {
        reportService.deleteReportedPost(postId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/users/violations")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserViolationCountDTO>> getUserViolationCounts() {
        return ResponseEntity.ok(reportService.getUserViolationCounts());
    }

    @GetMapping("/users/{userId}/violations")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ViolationDTO>> getUserViolations(@PathVariable Long userId) {
        return ResponseEntity.ok(reportService.getUserViolations(userId));
    }

    @PostMapping("/user/{userId}/ban")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDTO> banUser(@PathVariable Long userId) {
        return ResponseEntity.ok(reportService.banUser(userId));
    }

    @GetMapping("/posts")
    public ResponseEntity<Page<ReportedPostDTO>> getReportedPosts(
            @RequestParam(defaultValue = "pending") String status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(reportService.getReportedPosts(status, page, size));
    }
} 