package com.pixelwave.spring_boot.controller;

import com.pixelwave.spring_boot.DTO.UploadImageDTO;
import com.pixelwave.spring_boot.DTO.error.ErrorDTO;
import com.pixelwave.spring_boot.DTO.user.AddFriendRequestDTO;
import com.pixelwave.spring_boot.DTO.user.UpdateUserProfileRequestDTO;
import com.pixelwave.spring_boot.DTO.user.UserDetailResponseDTO;
import com.pixelwave.spring_boot.DTO.user.UserResponseDTO;
import com.pixelwave.spring_boot.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    //Get user by id
    @GetMapping("/user/{userId}")
    public ResponseEntity<UserDetailResponseDTO> getUserById(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.getUserById(userId));
    }
    //follow user
    @PostMapping("/user/{userId}/follow")
    public ResponseEntity<Void> followUser(@AuthenticationPrincipal UserDetails userDetails,
                                            @PathVariable Long userId) {
        userService.followUser(userDetails, userId);
        return ResponseEntity.status(201).build();
    }
    //unfollow user
    @PostMapping("/user/{userId}/unfollow")
    public ResponseEntity<Void> unfollowUser(@AuthenticationPrincipal UserDetails userDetails,
                                            @PathVariable Long userId) {
        userService.unfollowUser(userDetails, userId);
        return ResponseEntity.status(201).build();
    }
    //send add friend request
    @PostMapping("/user/{userId}/add-friend")
    public ResponseEntity<Void> addFriend(@AuthenticationPrincipal UserDetails userDetails,
                                            @PathVariable Long userId) {
        userService.addFriend(userDetails, userId);
        return ResponseEntity.status(201).build();
    }
    //get add friend requests of me
    @GetMapping("/user/friend-requests")
    public ResponseEntity<List<AddFriendRequestDTO>> getPendingFriendRequests(@AuthenticationPrincipal UserDetails userDetails) {
        var res = userService.getPendingFriendRequests(userDetails);
        return ResponseEntity.ok(res);
    }

    //accept friend request
    @PostMapping("/user/friend-request/{requestId}/accept")
    public ResponseEntity<Void> acceptFriendRequest(@AuthenticationPrincipal UserDetails userDetails,
                                                    @PathVariable Long requestId) {
        userService.acceptFriendRequest(userDetails, requestId);
        return ResponseEntity.status(200).build();
    }

    //reject friend request
    @PostMapping("/user/friend-request/{requestId}/reject")
    public ResponseEntity<Void> rejectFriendRequest(@AuthenticationPrincipal UserDetails userDetails,
                                                    @PathVariable Long requestId) {
        userService.rejectFriendRequest(userDetails, requestId);
        return ResponseEntity.status(200).build();
    }

    @PostMapping("/user/{userId}/block")
    public ResponseEntity<Void> blockUser(@AuthenticationPrincipal UserDetails userDetails,
                                           @PathVariable Long userId) {
        userService.blockUser(userDetails, userId);
        return ResponseEntity.status(201).build();
    }

    @PostMapping("/user/{userId}/unblock")
    public ResponseEntity<Void> unblockUser(@AuthenticationPrincipal UserDetails userDetails,
                                             @PathVariable Long userId) {
        userService.unblockUser(userDetails, userId);
        return ResponseEntity.status(201).build();
    }

    @GetMapping("/user/blocked-users")
    public ResponseEntity<List<UserDetailResponseDTO>> getBlockedUsers(@AuthenticationPrincipal UserDetails userDetails) {
        var res = userService.getBlockedUsers(userDetails);
        return ResponseEntity.status(200).body(res);
    }

    @PatchMapping(value = "/user/avatar", consumes = "multipart/form-data")
    public ResponseEntity<?> updateAvatar(@AuthenticationPrincipal UserDetails userDetails,
                                            @ModelAttribute UploadImageDTO imageDTO) {
        userService.updateAvatar(userDetails, imageDTO.getFile());
        return ResponseEntity.status(200).build();
    }

    @GetMapping("/user/me")
    public ResponseEntity<UserResponseDTO> getMe(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(userService.getMe(userDetails));
    }

    @PatchMapping("/user/profile")
    public ResponseEntity<UserResponseDTO> updateProfile(@AuthenticationPrincipal UserDetails userDetails,
                                                       @RequestBody UpdateUserProfileRequestDTO updateDTO) {
        return ResponseEntity.ok(userService.updateProfile(userDetails, updateDTO));
    }

}
