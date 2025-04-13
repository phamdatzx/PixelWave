package com.pixelwave.spring_boot.repository;

import com.pixelwave.spring_boot.model.Conservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ConservationRepository extends JpaRepository<Conservation, String> {
    // Custom query methods can be defined here if needed
    // For example, to find a conservation by user1 and user2:
    // Optional<Conservation> findByUser1AndUser2(User user1, User user2);

    boolean existsByUser1IdAndUser2IdOrUser1IdAndUser2Id(long user1Id, long user2Id, long user2IdAlt, long user1IdAlt);
}
