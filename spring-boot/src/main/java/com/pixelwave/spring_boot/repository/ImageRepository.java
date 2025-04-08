package com.pixelwave.spring_boot.repository;

import com.pixelwave.spring_boot.model.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {
    // Custom query methods can be defined here if needed
    // For example, to find images by post ID:
    // List<Image> findByPostId(Long postId);
}
