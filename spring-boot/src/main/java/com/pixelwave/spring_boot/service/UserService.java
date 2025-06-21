package com.pixelwave.spring_boot.service;

import com.pixelwave.spring_boot.DTO.user.AddFriendRequestDTO;
import com.pixelwave.spring_boot.DTO.user.UpdateUserProfileRequestDTO;
import com.pixelwave.spring_boot.DTO.user.UserDTO;
import com.pixelwave.spring_boot.DTO.user.UserDetailResponseDTO;
import com.pixelwave.spring_boot.DTO.user.UserRecommendationDTO;
import com.pixelwave.spring_boot.DTO.user.UserResponseDTO;
import com.pixelwave.spring_boot.exception.ConflictException;
import com.pixelwave.spring_boot.exception.ForbiddenException;
import com.pixelwave.spring_boot.exception.ResourceNotFoundException;
import com.pixelwave.spring_boot.model.NotificationType;
import com.pixelwave.spring_boot.model.User;
import com.pixelwave.spring_boot.model.UserAddFriendRequest;
import com.pixelwave.spring_boot.repository.UserAddFriendRequestRepository;
import com.pixelwave.spring_boot.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final UserAddFriendRequestRepository userAddFriendRequestRepository;
    private final ChatService chatService;
    private final S3Service s3Service;
    private final PasswordEncoder passwordEncoder;
    private final NotificationService notificationService;


    public UserDetailResponseDTO getUserById(UserDetails userDetails, Long userId) {
        var targetUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found id:" + userId));

        var currentUser = userRepository.findById(((User) userDetails).getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found id:" + ((User) userDetails).getId()));

        var resultDTO = modelMapper.map(targetUser, UserDetailResponseDTO.class);
        resultDTO.setPostCount(targetUser.getPosts().size());
        resultDTO.setFollowerCount(targetUser.getFollowers().size());
        resultDTO.setFollowingCount(targetUser.getFollowingUsers().size());
        resultDTO.setFriendCount(targetUser.getFriends().size());
        if(userDetails!= null && !Objects.equals(((User) userDetails).getId(), userId))
        {
            resultDTO.setIsFriend(targetUser.isFriendWith(((User) userDetails).getId()));
            resultDTO.setIsFollowing(targetUser.isFollowingBy(((User) userDetails)));
            resultDTO.setIsBlocked(currentUser.isBlocking(( targetUser)));
        }
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

        if(userAddFriendRequestRepository.findBySenderIdAndTargetIdAndStatus(((User) userDetails).getId(),userId,"PENDING") != null) {
            throw new ConflictException("You have already sent a friend request to this user");
        }

        UserAddFriendRequest addFriendRequest = UserAddFriendRequest.builder()
                .sender(currentUser)
                .target(targetUser)
                .status("PENDING")
                .createdAt(LocalDateTime.now())
                .build();

        if(!targetUser.getFollowers().contains(currentUser)) {
            targetUser.getFollowers().add(currentUser);
        }
        notificationService.sendNotification(targetUser, currentUser, NotificationType.NEW_FRIEND_REQUEST, currentUser.getId());

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

    public List<UserRecommendationDTO> getRecommendedUsers(UserDetails userDetails, int limit) {
        User currentUser = userRepository.findById(((User) userDetails).getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Get all users except current user and their friends
        List<User> allUsers = userRepository.findAll();
        Set<Long> currentUserFriendIds = currentUser.getFriends().stream()
                .map(User::getId)
                .collect(Collectors.toSet());
        currentUserFriendIds.add(currentUser.getId());

        // Filter out current user and their friends and user who are received friend requests from current user
        List<UserAddFriendRequest> friendRequests = userAddFriendRequestRepository.findAllBySenderIdAndStatus(currentUser.getId(), "PENDING");
        List<User> potentialRecommendations = allUsers.stream()
                .filter(user -> !currentUserFriendIds.contains(user.getId()) && friendRequests.stream().noneMatch(request -> request.getTarget().getId().equals(user.getId())))
                .toList();

        // Calculate mutual friends count for each potential recommendation
        Map<User, Integer> mutualFriendsCount = new HashMap<>();
        for (User potentialFriend : potentialRecommendations) {
            Set<Long> potentialFriendFriendIds = potentialFriend.getFriends().stream()
                    .map(User::getId)
                    .collect(Collectors.toSet());
            
            // Count mutual friends
            int mutualCount = (int) currentUser.getFriends().stream()
                    .filter(friend -> potentialFriendFriendIds.contains(friend.getId()))
                    .count();
            
            mutualFriendsCount.put(potentialFriend, mutualCount);
        }

        // Sort by mutual friends count (descending) and get top 9
        return mutualFriendsCount.entrySet().stream()
                .sorted(Map.Entry.<User, Integer>comparingByValue().reversed())
                .limit(limit)
                .map(entry -> UserRecommendationDTO.builder()
                        .id(entry.getKey().getId())
                        .fullName(entry.getKey().getFullName())
                        .avatar(entry.getKey().getAvatar())
                        .mutualFriendsCount(entry.getValue())
                        .build())
                .collect(Collectors.toList());
    }

    public List<UserDTO> getFriends(Long userID, String searchTerm) {
        User currentUser = userRepository.findById(userID)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<User> friends;
        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            friends = userRepository.findFriendsByUserIdAndSearchTerm(currentUser.getId(), searchTerm.trim());
        } else {
            friends = currentUser.getFriends();
        }

        return friends.stream()
                .map(friend -> modelMapper.map(friend, UserDTO.class))
                .collect(Collectors.toList());
    }

    public List<UserDTO> getFollowers(Long userID, String searchTerm) {
        User currentUser = userRepository.findById(userID)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<User> followers;
        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            followers = userRepository.findFollowersByUserIdAndSearchTerm(currentUser.getId(), searchTerm.trim());
        } else {
            followers = currentUser.getFriends();
        }

        return followers.stream()
                .map(friend -> modelMapper.map(friend, UserDTO.class))
                .collect(Collectors.toList());
    }


    public void deleteFriend(UserDetails userDetails, Long userId) {
        var currentUser = userRepository.findById(((User) userDetails).getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found id:" + ((User) userDetails).getId()));

        var targetUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found id:" + userId));

        if (!currentUser.getFriends().contains(targetUser)) {
            throw new ConflictException("You are not friends with this user");
        }

        currentUser.getFriends().remove(targetUser);
        targetUser.getFriends().remove(currentUser);

        userRepository.save(currentUser);
        userRepository.save(targetUser);
    }

    public void changePassword(UserDetails userDetails, String oldPassword,String newPassword) {
        User user = userRepository.findById(((User) userDetails).getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if(!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new ConflictException("Old password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
}
