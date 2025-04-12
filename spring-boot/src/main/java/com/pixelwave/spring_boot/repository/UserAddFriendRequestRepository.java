package com.pixelwave.spring_boot.repository;

import com.pixelwave.spring_boot.model.UserAddFriendRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserAddFriendRequestRepository extends JpaRepository<UserAddFriendRequest, Long> {

}
