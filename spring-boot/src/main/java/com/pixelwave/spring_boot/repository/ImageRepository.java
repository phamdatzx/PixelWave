package com.pixelwave.spring_boot.repository;

import com.pixelwave.spring_boot.model.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {
    // Custom query methods can be defined here if needed
    // For example, to find images by post ID:
    // List<Image> findByPostId(Long postId);

    @Modifying
    @Query(value = """
        DELETE FROM tag_images WHERE images_id = :imageId;
        """, nativeQuery = true)
    void deleteTagsOfImageById(Long imageId);

    @Modifying
    @Query(value = """
        DELETE FROM image WHERE comment_id = :commentId;
        """, nativeQuery = true)
    void deleteImagesOfComment(Long commentId);


}
