package com.pixelwave.spring_boot.repository;

import com.pixelwave.spring_boot.DTO.PostRecommendationDTO;
import com.pixelwave.spring_boot.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    Page<Post> findByUserId(Long userId, Pageable pageable);

    Page<Post> findByUserIdAndPrivacySettingIn(Long userId,List<String> privacySettingList, Pageable pageable);

    Page<Post> findByUserIdAndPrivacySetting(Long userId,String privacySetting, Pageable pageable);

    Page<Post> findByTaggedUsersIdAndPrivacySettingIn(Long userId, List<String> privacySetting, Pageable pageable);

    @Query(value = """
SELECT
    p.id AS id,
    p.caption,
    p.created_at,
    p.privacy_setting,
    p.like_count,
    p.comment_count,
    u.id AS user_id,
    u.full_name AS user_fullname,
    u.avatar AS user_avatar,
    COUNT(DISTINCT pv.created_at) AS view_count,
    CASE WHEN pt_you.post_id IS NOT NULL THEN TRUE ELSE FALSE END AS is_tag_you,
    COUNT(DISTINCT pt_all.user_id) AS tag_user_count,
    img.id AS image_id,
    img.url AS image_url,
    CASE WHEN pl.post_id IS NOT NULL THEN TRUE ELSE FALSE END AS is_liked
FROM post p
         JOIN users u ON p.post_user_id = u.id
         LEFT JOIN post_view pv ON p.id = pv.post_id AND pv.user_id = :queryUserId
         LEFT JOIN post_tag pt_you ON p.id = pt_you.post_id AND pt_you.user_id = :queryUserId
         LEFT JOIN post_tag pt_all ON p.id = pt_all.post_id
         LEFT JOIN post_like pl ON p.id = pl.post_id AND pl.user_id = :queryUserId
         LEFT JOIN image img ON p.id = img.post_id
WHERE
    (
        p.post_user_id IN (
            SELECT user_id
            FROM user_followers
            WHERE follower_id = :queryUserId
        )
            AND (
            p.privacy_setting = 'public'
                OR (:isFriend = TRUE AND p.privacy_setting IN ('public', 'friend'))
            )
        )
   OR pt_you.user_id = :queryUserId
GROUP BY
    p.id,
    p.created_at,
    p.post_user_id,
    p.privacy_setting,
    u.id,
    u.username,
    u.full_name,
    is_tag_you,
    img.id,
    img.url,
    is_liked
ORDER BY view_count, p.created_at DESC
LIMIT :limit;
        """, nativeQuery = true)
    List<Object[]> findPostsWithImages(
            @Param("queryUserId") Long queryUserId,
            @Param("isFriend") boolean isFriend,
            @Param("limit") int limit);

    @Modifying
    @Query(value = """
DELETE FROM post_like w where w.post_id = :postId and w.user_id = :userId
        """, nativeQuery = true)
    void unlikePost(Long postId, Long userId);

    // Method 1: Individual delete methods for each relationship table
    @Modifying
    @Query(value = "DELETE FROM post_like WHERE post_id = :postId", nativeQuery = true)
    void deletePostLikes(@Param("postId") Long postId);

    @Modifying
    @Query(value = "DELETE FROM post_tag WHERE post_id = :postId", nativeQuery = true)
    void deletePostTags(@Param("postId") Long postId);

    @Modifying
    @Query(value = "DELETE FROM post_view WHERE post_id = :postId", nativeQuery = true)
    void deletePostViews(@Param("postId") Long postId);

    @Modifying
    @Query(value = "DELETE FROM collection_post WHERE post_id = :postId", nativeQuery = true)
    void deletePostFromCollections(@Param("postId") Long postId);

    // Method 2: Single method to delete from all relationship tables at once
    @Modifying
    @Query(value = """
        DELETE FROM post_like WHERE post_id = :postId;
        DELETE FROM post_tag WHERE post_id = :postId;
        DELETE FROM post_view WHERE post_id = :postId;
        DELETE FROM collection_post WHERE post_id = :postId;
        """, nativeQuery = true)
    void deletePostFromAllRelationships(@Param("postId") Long postId);

    // Method 3: Complete post deletion with all relationships
    @Modifying
    @Query(value = """
        DELETE FROM post_like WHERE post_id = :postId;
        DELETE FROM post_tag WHERE post_id = :postId;
        DELETE FROM post_view WHERE post_id = :postId;
        DELETE FROM collection_post WHERE post_id = :postId;
        DELETE FROM comment WHERE post_id = :postId;
        DELETE FROM image WHERE post_id = :postId;
        DELETE FROM post WHERE id = :postId;
        """, nativeQuery = true)
    void deletePostCompletely(@Param("postId") Long postId);

    @Query(value = """
    SELECT *, ts_rank(to_tsvector('english', caption), websearch_to_tsquery('english', :text)) AS rank
    FROM post
    WHERE to_tsvector('english', caption) @@ websearch_to_tsquery('english', :text) 
    AND privacy_setting IN ('public', 'friend')
    ORDER BY rank DESC
    """,
            countQuery = """
    SELECT COUNT(*)
    FROM post
    WHERE to_tsvector('english', caption) @@ websearch_to_tsquery('english', :text)
    AND privacy_setting IN ('public', 'friend')
    """,
            nativeQuery = true)
    Page<Post> searchByCaptionWebsearch(@Param("text") String text, Pageable pageable);


}
