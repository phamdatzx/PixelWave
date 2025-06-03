package com.pixelwave.spring_boot.repository;

import com.pixelwave.spring_boot.model.Conversation;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, String> {
    // Custom query methods can be defined here if needed
    // For example, to find a conservation by user1 and user2:
    // Optional<Conservation> findByUser1AndUser2(User user1, User user2);

    boolean existsByUser1IdAndUser2IdOrUser1IdAndUser2Id(long user1Id, long user2Id, long user2IdAlt, long user1IdAlt);

    @Query("SELECT c FROM Conversation c WHERE (c.user1.id = :userId1 OR c.user2.id = :userId2) AND " +
            "(:searchTerm = '' OR LOWER(c.user1.username) LIKE %:searchTerm% OR LOWER(c.user2.username) LIKE %:searchTerm%)")
    List<Conversation> findByUserIdWithSearch(
            @Param("userId1") Long userId1,
            @Param("userId2") Long userId2,
            @Param("searchTerm") String searchTerm,
            Pageable pageable);
}
