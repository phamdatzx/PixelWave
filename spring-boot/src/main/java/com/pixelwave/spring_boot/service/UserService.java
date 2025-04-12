package com.pixelwave.spring_boot.service;

import com.pixelwave.spring_boot.DTO.user.UserDetailResponseDTO;
import com.pixelwave.spring_boot.exception.ConflictException;
import com.pixelwave.spring_boot.exception.ResourceNotFoundException;
import com.pixelwave.spring_boot.model.User;
import com.pixelwave.spring_boot.model.UserAddFriendRequest;
import com.pixelwave.spring_boot.repository.UserAddFriendRequestRepository;
import com.pixelwave.spring_boot.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final UserAddFriendRequestRepository userAddFriendRequestRepository;

    public UserDetailResponseDTO getUserById(Long userId) {
        var targetUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found id:" + userId));

        var resultDTO = modelMapper.map(targetUser, UserDetailResponseDTO.class);
        resultDTO.setPostCount(targetUser.getPosts().size());
        resultDTO.setFollowerCount(targetUser.getFollowers().size());
        resultDTO.setFollowingCount(targetUser.getFollowingUsers().size());
        resultDTO.setFriendCount(targetUser.getFriends().size());

        return resultDTO;
    }

    public void followUser(UserDetails userDetails, Long userId) {
        var currentUser = userRepository.findById(((User) userDetails).getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found id:" + ((User) userDetails).getId()));

        var targetUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found id:" + userId));

        if (currentUser.getFollowingUsers().contains(targetUser)) {
            throw new ConflictException("You are already following this user");
        }

        currentUser.getFollowingUsers().add(targetUser);

        userRepository.save(currentUser);
    }

    public void unfollowUser(UserDetails userDetails, Long userId) {
        var currentUser = userRepository.findById(((User) userDetails).getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found id:" + ((User) userDetails).getId()));

        var targetUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found id:" + userId));

        if (!currentUser.getFollowingUsers().contains(targetUser)) {
            throw new ConflictException("You are not following this user");
        }

        currentUser.getFollowingUsers().remove(targetUser);

        userRepository.save(currentUser);
    }

    public void addFriend(UserDetails userDetails, Long userId) {
        var currentUser = userRepository.findById(((User) userDetails).getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found id:" + ((User) userDetails).getId()));

        var targetUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found id:" + userId));

        if (targetUser.getFriends().contains(targetUser)) {
            throw new ConflictException("You are already friends with this user");
        }

        UserAddFriendRequest addFriendRequest = UserAddFriendRequest.builder()
                .sender(currentUser)
                .target(targetUser)
                .status("PENDING")
                .createdAt(LocalDateTime.now())
                .build();

        userAddFriendRequestRepository.save(addFriendRequest);
    }
}
