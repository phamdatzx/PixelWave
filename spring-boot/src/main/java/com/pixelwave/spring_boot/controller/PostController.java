package com.pixelwave.spring_boot.controller;

import com.pixelwave.spring_boot.DTO.post.*;
import com.pixelwave.spring_boot.DTO.user.UserDTO;
import com.pixelwave.spring_boot.model.User;
import com.pixelwave.spring_boot.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping("/post")
    public ResponseEntity<PostDetailDTO> uploadPost(@AuthenticationPrincipal UserDetails userDetails ,
                                                      @Valid @ModelAttribute UploadPostDTO uploadPostDTO) {
        var postResponseDTO = postService.uploadPost(userDetails, uploadPostDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(postResponseDTO);
    }

    @GetMapping("/post/{postId}")
    public ResponseEntity<PostDetailDTO> getPostById(@AuthenticationPrincipal UserDetails userDetails ,
                                                       @PathVariable Long postId) {
        return ResponseEntity.ok(postService.getPostById(userDetails, postId));
    }

    @PostMapping("/post/{postId}/like")
    public ResponseEntity<Void> likePost(@AuthenticationPrincipal UserDetails userDetails ,
                                          @PathVariable Long postId) {
        postService.likePost(userDetails, postId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/post/{postId}/unlike")
    public ResponseEntity<Void> unlikePost(@AuthenticationPrincipal UserDetails userDetails ,
                                          @PathVariable Long postId) {
        postService.unlikePost(userDetails, postId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/user/{userId}/posts")
    public ResponseEntity<PostSimplePageDTO> getUserPosts(@AuthenticationPrincipal UserDetails userDetails ,
                                                          @PathVariable Long userId,
                                                          @RequestParam(defaultValue = "1") int page,
                                                          @RequestParam(defaultValue = "10") int size,
                                                          @RequestParam(defaultValue = "createdAt") String sortBy,
                                                          @RequestParam(defaultValue = "desc") String sortDirection){
        return ResponseEntity.ok(postService.getUserPosts(userDetails, userId, page, size, sortBy, sortDirection));
    }

    @GetMapping("/user/{userId}/tagged-posts")
    public ResponseEntity<PostSimplePageDTO> getUserTaggedPosts(@AuthenticationPrincipal UserDetails userDetails ,
                                                          @PathVariable Long userId,
                                                          @RequestParam(defaultValue = "1") int page,
                                                          @RequestParam(defaultValue = "10") int size,
                                                          @RequestParam(defaultValue = "createdAt") String sortBy,
                                                          @RequestParam(defaultValue = "desc") String sortDirection){
        return ResponseEntity.ok(postService.getUserTaggedPosts(userDetails, userId, page, size, sortBy, sortDirection));
    }

    @GetMapping("/feed")
    public ResponseEntity<List<PostDetailDTO>> getFeed(@AuthenticationPrincipal UserDetails userDetails,
                                                               @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(postService.getFeedPosts(((User) userDetails).getId(), true, size));
    }

    @DeleteMapping("/post/{postId}")
    public ResponseEntity<Void> deletePost(@AuthenticationPrincipal UserDetails userDetails,
                                           @PathVariable Long postId) {
        postService.deletePost(userDetails, postId);
        return ResponseEntity.status(200).build();
    }

    @GetMapping("/post/{postId}/tagged-users")
    public ResponseEntity<List<UserDTO>> getTaggedUsers(@AuthenticationPrincipal UserDetails userDetails,
                                                        @PathVariable Long postId) {
        List<UserDTO> taggedUsers = postService.getTaggedUsers(userDetails, postId);
        return ResponseEntity.ok(taggedUsers);
    }

    @GetMapping("/posts/search")
    public ResponseEntity<Page<PostDetailDTO>> searchPosts(@AuthenticationPrincipal UserDetails userDetails,
                                                           @RequestParam String query,
                                                           @RequestParam(defaultValue = "1") int page,
                                                           @RequestParam(defaultValue = "10") int size,
                                                           @RequestParam(defaultValue = "created_at") String sortBy,
                                                           @RequestParam(defaultValue = "desc") String sortDirection) {
        return ResponseEntity.ok(postService.searchPosts(userDetails, query, page, size, sortBy, sortDirection));
    }
}
