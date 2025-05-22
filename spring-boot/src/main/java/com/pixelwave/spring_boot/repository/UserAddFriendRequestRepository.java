package com.pixelwave.spring_boot.repository;

import com.pixelwave.spring_boot.model.UserAddFriendRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserAddFriendRequestRepository extends JpaRepository<UserAddFriendRequest, Long> {
    List<UserAddFriendRequest> findAllByTargetIdAndStatusOrderByCreatedAtDesc(Long targetId, String status);

    UserAddFriendRequest findBySenderIdAndTargetIdAndStatus(Long senderId, Long targetId, String status);

    List<UserAddFriendRequest> findAllBySenderIdAndStatus(Long senderId, String status);
}
