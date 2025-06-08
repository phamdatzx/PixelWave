package com.pixelwave.spring_boot.repository;

import com.pixelwave.spring_boot.model.Violation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ViolationRepository extends JpaRepository<Violation, Long> {

    @Query("SELECT v.reporter as user, COUNT(v) as violationCount " +
           "FROM Violation v " +
           "GROUP BY v.reporter " +
           "ORDER BY violationCount DESC")
    List<Object[]> findUserViolationCounts();

    List<Violation> findByReporterId(Long reporterId);
} 