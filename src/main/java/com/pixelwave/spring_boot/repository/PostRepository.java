package com.pixelwave.spring_boot.repository;

import com.pixelwave.spring_boot.DTO.PostRecommendationDTO;
import com.pixelwave.spring_boot.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    Page<Post> findByUserId(Long userId, Pageable pageable);

    Page<Post> findByUserIdAndPrivacySettingIn(Long userId,List<String> privacySettingList, Pageable pageable);

    Page<Post> findByUserIdAndPrivacySetting(Long userId,String privacySetting, Pageable pageable);

    @Query(value = """
    SELECT * FROM (
        SELECT
            p.id,
            p.caption,
            p.privacy_setting,
            p.created_at,
            p.comment_count,
            p.like_count,
            pu.id AS user_id,
            pu.avatar,
            pu.full_name,
            COALESCE(pvc.view_count, 0) AS user_view_count
        FROM post AS p
            FULL JOIN post_view AS pv ON p.id = pv.post_id
            JOIN users AS pu ON p.post_user_id = pu.id
            LEFT JOIN (
                SELECT post_id, COUNT(*) AS view_count
                FROM post_view
                WHERE user_id = :user_id
                GROUP BY post_id
            ) AS pvc ON p.id = pvc.post_id
        WHERE
            pu.id IN (
                SELECT uf.user_id
                FROM user_followers uf
                    JOIN users qu ON qu.id = uf.follower_id
                WHERE qu.id = :user_id
            )
            AND p.privacy_setting != 'private'
            AND NOT EXISTS (
                SELECT 1
                FROM post_view
                WHERE post_view.post_id = p.id
                AND post_view.user_id = :user_id
                AND post_view.created_at >= NOW() - INTERVAL '24 hours'
            )
        UNION
        SELECT
            p.id,
            p.caption,
            p.privacy_setting,
            p.created_at,
            p.comment_count,
            p.like_count,
            pu.id AS user_id,
            pu.avatar,
            pu.full_name,
            COALESCE(pvc.view_count, 0) AS user_view_count
        FROM post AS p
            FULL JOIN post_view AS pv ON p.id = pv.post_id
            JOIN post_tag AS pt ON p.id = pt.post_id
            JOIN users AS tu ON pt.user_id = tu.id
            JOIN users AS pu ON p.post_user_id = pu.id
            LEFT JOIN (
                SELECT post_id, COUNT(*) AS view_count
                FROM post_view
                WHERE user_id = :user_id
                GROUP BY post_id
            ) AS pvc ON p.id = pvc.post_id
        WHERE
            tu.id IN (
                SELECT uf.user_id
                FROM user_followers uf
                    JOIN users qu ON qu.id = uf.follower_id
                WHERE qu.id = :user_id
            )
            AND p.privacy_setting != 'private'
            AND NOT EXISTS (
                SELECT 1
                FROM post_view
                WHERE post_view.post_id = p.id
                AND post_view.user_id = :user_id
                AND post_view.created_at >= NOW() - INTERVAL '24 hours'
            )
    ) AS combined_posts
    ORDER BY user_view_count ASC, created_at DESC
""", nativeQuery = true)
    List<PostRecommendationDTO> findRecommendedPosts(@Param("user_id") Long userId);




}
