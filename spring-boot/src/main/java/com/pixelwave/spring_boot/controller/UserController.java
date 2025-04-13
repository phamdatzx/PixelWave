package com.pixelwave.spring_boot.controller;

import com.pixelwave.spring_boot.DTO.user.AddFriendRequestDTO;
import com.pixelwave.spring_boot.DTO.user.UserDetailResponseDTO;
import com.pixelwave.spring_boot.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

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




}
