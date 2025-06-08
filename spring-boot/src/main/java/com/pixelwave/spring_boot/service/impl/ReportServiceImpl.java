package com.pixelwave.spring_boot.service.impl;

import com.pixelwave.spring_boot.DTO.report.CreateReportDTO;
import com.pixelwave.spring_boot.DTO.report.ReportDTO;
import com.pixelwave.spring_boot.DTO.user.UserDTO;
import com.pixelwave.spring_boot.DTO.user.UserViolationCountDTO;
import com.pixelwave.spring_boot.DTO.violation.ViolationDTO;
import com.pixelwave.spring_boot.exception.BadRequestException;
import com.pixelwave.spring_boot.exception.ResourceNotFoundException;
import com.pixelwave.spring_boot.model.Post;
import com.pixelwave.spring_boot.model.Report;
import com.pixelwave.spring_boot.model.ReportStatus;
import com.pixelwave.spring_boot.model.User;
import com.pixelwave.spring_boot.model.Violation;
import com.pixelwave.spring_boot.repository.PostRepository;
import com.pixelwave.spring_boot.repository.ReportRepository;
import com.pixelwave.spring_boot.repository.UserRepository;
import com.pixelwave.spring_boot.repository.ViolationRepository;
import com.pixelwave.spring_boot.service.PostService;
import com.pixelwave.spring_boot.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final ReportRepository reportRepository;
    private final PostRepository postRepository;
    private final ModelMapper modelMapper;
    private final PostService postService;
    private final ViolationRepository violationRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public ReportDTO createReport(CreateReportDTO createReportDTO, User reporter) {
        Post post = postRepository.findById(createReportDTO.getPostId())
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));

        // Check if user has already reported this post
        if (reportRepository.existsByPostAndReporter(post, reporter)) {
            throw new BadRequestException("You have already reported this post");
        }

        Report report = Report.builder()
                .reporter(reporter)
                .post(post)
                .reason(createReportDTO.getReason())
                .description(createReportDTO.getDescription())
                .build();

        report = reportRepository.save(report);
        return convertToDTO(report);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReportDTO> getReports(ReportStatus status, Pageable pageable) {
        Page<Report> reports;
        if (status != null) {
            reports = reportRepository.findByStatus(status, pageable);
        } else {
            reports = reportRepository.findAll(pageable);
        }
        return reports.map(this::convertToDTO);
    }

    @Override
    @Transactional
    public ReportDTO updateReportStatus(Long reportId, ReportStatus newStatus) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new ResourceNotFoundException("Report not found"));

        report.setStatus(newStatus);
        report = reportRepository.save(report);
        return convertToDTO(report);
    }

    @Override
    @Transactional
    public void deleteReportedPost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));

        List<Report> reportList = reportRepository.findByPost(post);

        // Create violation records from reports
        List<Violation> violations = reportList.stream()
                .map(report -> Violation.builder()
                        .reporter(report.getReporter())
                        .reason(report.getReason())
                        .description(report.getDescription())
                        .createdAt(LocalDateTime.now())
                        .build())
                .toList();

        // Save violations
        violationRepository.saveAll(violations);

        // Delete reports
        reportRepository.deleteAll(reportList);

        // Delete post
        postService.deletePost(postId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserViolationCountDTO> getUserViolationCounts() {
        return violationRepository.findUserViolationCounts().stream()
                .map(result -> {
                    User user = (User) result[0];
                    Long count = (Long) result[1];
                    return UserViolationCountDTO.builder()
                            .user(modelMapper.map(user, UserDTO.class))
                            .violationCount(count)
                            .build();
                })
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ViolationDTO> getUserViolations(Long userId) {
        return violationRepository.findByReporterId(userId).stream()
                .map(this::convertToDTO)
                .toList();
    }

    @Override
    @Transactional
    public UserDTO banUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (user.getIsBanned()) {
            throw new BadRequestException("User is already banned");
        }

        user.setIsBanned(true);
        user = userRepository.save(user);
        return modelMapper.map(user, UserDTO.class);
    }

    private ReportDTO convertToDTO(Report report) {
        ReportDTO dto = modelMapper.map(report, ReportDTO.class);
        dto.setReporter(modelMapper.map(report.getReporter(), UserDTO.class));
        dto.setPostId(report.getPost().getId());
        return dto;
    }

    private ViolationDTO convertToDTO(Violation violation) {
        ViolationDTO dto = modelMapper.map(violation, ViolationDTO.class);
        dto.setReporter(modelMapper.map(violation.getReporter(), UserDTO.class));
        return dto;
    }
} 