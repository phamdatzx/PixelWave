package com.pixelwave.spring_boot.service;

import com.pixelwave.spring_boot.DTO.user.AddFriendRequestDTO;
import com.pixelwave.spring_boot.DTO.user.UserDetailResponseDTO;
import com.pixelwave.spring_boot.exception.ConflictException;
import com.pixelwave.spring_boot.exception.ForbiddenException;
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
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final UserAddFriendRequestRepository userAddFriendRequestRepository;
    private final ChatService chatService;

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

    public List<AddFriendRequestDTO> getPendingFriendRequests(UserDetails userDetails) {
        List<UserAddFriendRequest> requests = userAddFriendRequestRepository.findAllByTargetIdAndStatusOrderByCreatedAtDesc(((User) userDetails).getId(), "PENDING");

        return requests.stream()
                .map(request -> {
                    return modelMapper.map(request, AddFriendRequestDTO.class);
                })
                .toList();
    }

    public void acceptFriendRequest(UserDetails userDetails, long requestId){
        var targetRequest = userAddFriendRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Friend request not found id:" + requestId));

        if(!targetRequest.getTarget().getId().equals(((User)userDetails).getId())) {
            throw new ForbiddenException("You are not the target of this request");
        }

        if(!targetRequest.getStatus().equals("PENDING")) {
            throw new ConflictException("Only pending requests can be accepted");
        }

        targetRequest.getSender().addFriend(targetRequest.getTarget());
        chatService.createConservation(targetRequest);

        targetRequest.setStatus("ACCEPTED");
        userAddFriendRequestRepository.save(targetRequest);
    }

    public void rejectFriendRequest(UserDetails userDetails, Long requestId) {
        var targetRequest = userAddFriendRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Friend request not found id:" + requestId));

        if(!targetRequest.getTarget().getId().equals(((User)userDetails).getId())) {
            throw new ForbiddenException("You are not the target of this request");
        }

        if(!targetRequest.getStatus().equals("PENDING")) {
            throw new ConflictException("Only pending requests can be rejected");
        }

        targetRequest.setStatus("REJECTED");
        userAddFriendRequestRepository.save(targetRequest);
    }
}
