package com.pixelwave.spring_boot.service.impl;

import com.pixelwave.spring_boot.DTO.post.PostDetailDTO;
import com.pixelwave.spring_boot.DTO.post.ReportedPostDTO;
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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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

    @Override
    @Transactional(readOnly = true)
    public Page<ReportedPostDTO> getReportedPosts(String status, int page, int size) {
        ReportStatus reportStatus = status != null ? ReportStatus.valueOf(status.toUpperCase()) : null;
        Pageable pageable = PageRequest.of(page - 1, size);
        
        Page<Object[]> reportedPosts = reportRepository.findPostsWithReportCount(reportStatus, pageable);
        
        List<ReportedPostDTO> reportedPostDTOs = reportedPosts.getContent().stream()
            .map(result -> {
                Post post = (Post) result[0];
                Long reportCount = (Long) result[1];
                
                // Get all reports for this post
                List<Report> reports = reportRepository.findByPost(post);
                
                return ReportedPostDTO.builder()
                    .postId(post.getId())
                    .caption(post.getCaption())
                    .authorUsername(post.getUser().getUsername())
                    .postCreatedAt(post.getCreatedAt())
                    .reportCount(reportCount.intValue())
                    .reports(reports.stream()
                        .map(report -> ReportedPostDTO.ReportDetailDTO.builder()
                            .reportId(report.getId())
                            .reporterUsername(report.getReporter().getUsername())
                            .reason(report.getReason())
                            .description(report.getDescription())
                            .status(report.getStatus())
                            .reportedAt(report.getCreatedAt())
                            .build())
                        .collect(Collectors.toList()))
                    .build();
            })
            .collect(Collectors.toList());

        return new PageImpl<>(reportedPostDTOs, pageable, reportedPosts.getTotalElements());
    }

    @Override
    public void unbanUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setIsBanned(false);
        userRepository.save(user);
    }

    @Override
    public List<UserDTO> getBannedUsers() {
        userRepository.findAllByIsBanned(true);
        return userRepository.findAllByIsBanned(true).stream()
                .map(user -> modelMapper.map(user, UserDTO.class))
                .collect(Collectors.toList());
    }

    private ReportDTO convertToDTO(Report report) {
        return ReportDTO.builder()
                .id(report.getId())
                .postId(report.getPost().getId())
                .reporterId(report.getReporter().getId())
                .reason(report.getReason())
                .description(report.getDescription())
                .status(report.getStatus())
                .createdAt(report.getCreatedAt())
                .updatedAt(report.getUpdatedAt())
                .build();
    }

    private ViolationDTO convertToDTO(Violation violation) {
        ViolationDTO dto = modelMapper.map(violation, ViolationDTO.class);
        dto.setReporter(modelMapper.map(violation.getReporter(), UserDTO.class));
        return dto;
    }
} 