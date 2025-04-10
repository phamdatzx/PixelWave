package com.pixelwave.spring_boot.repository;

import com.pixelwave.spring_boot.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    Page<Post> findByUserId(Long userId, Pageable pageable);

    Page<Post> findByUserIdAndPrivacySettingIn(Long userId,List<String> privacySettingList, Pageable pageable);

    Page<Post> findByUserIdAndPrivacySetting(Long userId,String privacySetting, Pageable pageable);

}
