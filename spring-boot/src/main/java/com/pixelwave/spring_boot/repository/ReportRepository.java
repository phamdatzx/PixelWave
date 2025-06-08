package com.pixelwave.spring_boot.repository;

import com.pixelwave.spring_boot.model.Post;
import com.pixelwave.spring_boot.model.Report;
import com.pixelwave.spring_boot.model.ReportStatus;
import com.pixelwave.spring_boot.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {
    List<Report> findByPost(Post post);
    List<Report> findByReporter(User reporter);
    Page<Report> findByStatus(ReportStatus status, Pageable pageable);
    boolean existsByPostAndReporter(Post post, User reporter);
} 