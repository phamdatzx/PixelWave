package com.pixelwave.spring_boot.repository;

import com.pixelwave.spring_boot.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    Boolean existsByUsername(String username);

    List<User> findAllByIdIn(List<Long> ids);

    @Query("SELECT f FROM User u JOIN u.friends f WHERE u.id = :userId AND LOWER(f.fullName) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<User> findFriendsByUserIdAndSearchTerm(@Param("userId") Long userId, @Param("searchTerm") String searchTerm);

    @Query("SELECT f FROM User u JOIN u.followers f WHERE u.id = :userId AND LOWER(f.fullName) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<User> findFollowersByUserIdAndSearchTerm(@Param("userId") Long userId, @Param("searchTerm") String searchTerm);
}
