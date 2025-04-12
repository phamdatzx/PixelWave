package com.pixelwave.spring_boot.controller;

import com.pixelwave.spring_boot.DTO.user.UserDetailResponseDTO;
import com.pixelwave.spring_boot.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

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
//    @GetMapping("/user/friend-requests")
//    public ResponseEntity<Void> getFriendRequests(@AuthenticationPrincipal UserDetails userDetails) {
//        userService.getFriendRequests(userDetails);
//        return ResponseEntity.status(201).build();
//    }

    //accept friend request

    //reject friend request




}
