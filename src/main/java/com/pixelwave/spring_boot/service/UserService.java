package com.pixelwave.spring_boot.service;

import com.pixelwave.spring_boot.DTO.user.AddFriendRequestDTO;
import com.pixelwave.spring_boot.DTO.user.UpdateUserProfileRequestDTO;
import com.pixelwave.spring_boot.DTO.user.UserDetailResponseDTO;
import com.pixelwave.spring_boot.DTO.user.UserResponseDTO;
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
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final UserAddFriendRequestRepository userAddFriendRequestRepository;
    private final ChatService chatService;
    private final S3Service s3Service;

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

    public void blockUser(UserDetails userDetails, Long userId) {
        var currentUser = userRepository.findById(((User) userDetails).getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found id:" + ((User) userDetails).getId()));

        var targetUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found id:" + userId));

        if (currentUser.getBlockedUsers().contains(targetUser)) {
            throw new ConflictException("You are already blocking this user");
        }

        currentUser.getBlockedUsers().add(targetUser);

        userRepository.save(currentUser);
    }

    public void unblockUser(UserDetails userDetails, Long userId) {
        var currentUser = userRepository.findById(((User) userDetails).getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found id:" + ((User) userDetails).getId()));

        var targetUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found id:" + userId));

        if (!currentUser.getBlockedUsers().contains(targetUser)) {
            throw new ConflictException("You are not blocking this user");
        }

        currentUser.getBlockedUsers().remove(targetUser);

        userRepository.save(currentUser);
    }

    public List<UserDetailResponseDTO> getBlockedUsers(UserDetails userDetails) {
        var currentUser = userRepository.findById(((User) userDetails).getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found id:" + ((User) userDetails).getId()));

        return currentUser.getBlockedUsers().stream()
                .map(user -> modelMapper.map(user, UserDetailResponseDTO.class))
                .toList();
    }

    public void updateAvatar(UserDetails userDetails, MultipartFile file) {
        // Get current user
        User currentUser = userRepository.findById(((User) userDetails).getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Upload file to S3
        String avatarUrl = s3Service.uploadFile(file, "avatars");

        // Update user's avatar URL
        currentUser.setAvatar(avatarUrl);
        userRepository.save(currentUser);
    }

    public UserResponseDTO getMe(UserDetails userDetails) {
        var currentUser = userRepository.findById(((User) userDetails).getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found id:" + ((User) userDetails).getId()));
        return modelMapper.map(currentUser, UserResponseDTO.class);
    }

    public UserResponseDTO updateProfile(UserDetails userDetails, UpdateUserProfileRequestDTO updateDTO) {
        var currentUser = userRepository.findById(((User) userDetails).getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found id:" + ((User) userDetails).getId()));

        if (updateDTO.getPhoneNumber() != null) {
            currentUser.setPhoneNumber(updateDTO.getPhoneNumber());
        }
        if (updateDTO.getAge() != null) {
            currentUser.setAge(updateDTO.getAge());
        }
        if (updateDTO.getGender() != null) {
            currentUser.setGender(updateDTO.getGender());
        }
        if (updateDTO.getBio() != null) {
            currentUser.setBio(updateDTO.getBio());
        }

        return modelMapper.map(userRepository.save(currentUser), UserResponseDTO.class);
    }
}
