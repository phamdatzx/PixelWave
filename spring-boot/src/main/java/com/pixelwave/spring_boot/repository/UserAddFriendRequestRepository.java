package com.pixelwave.spring_boot.repository;

import com.pixelwave.spring_boot.model.UserAddFriendRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserAddFriendRequestRepository extends JpaRepository<UserAddFriendRequest, Long> {
    List<UserAddFriendRequest> findAllByTargetIdAndStatusOrderByCreatedAtDesc(Long targetId, String status);
}
