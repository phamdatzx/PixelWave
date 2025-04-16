package com.pixelwave.spring_boot.controller;

import com.pixelwave.spring_boot.DTO.post.PostResponseDTO;
import com.pixelwave.spring_boot.DTO.post.PostResponsesPageDTO;
import com.pixelwave.spring_boot.DTO.post.UploadPostDTO;
import com.pixelwave.spring_boot.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping("/post")
    public ResponseEntity<PostResponseDTO> uploadPost(@AuthenticationPrincipal UserDetails userDetails ,
                                                      @Valid @ModelAttribute UploadPostDTO uploadPostDTO) {
        var postResponseDTO = postService.uploadPost(userDetails, uploadPostDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(postResponseDTO);
    }

    @GetMapping("/post/{postId}")
    public ResponseEntity<PostResponseDTO> getPostById(@AuthenticationPrincipal UserDetails userDetails ,
                                                       @PathVariable Long postId) {
        return ResponseEntity.ok(postService.getPostById(userDetails, postId));
    }

    @PostMapping("/post/{postId}/toggle-like")
    public ResponseEntity<Void> toggleLikePost(@AuthenticationPrincipal UserDetails userDetails ,
                                          @PathVariable Long postId) {
        postService.toggleLikePost(userDetails, postId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    @GetMapping("/user/{userId}/posts")
    public ResponseEntity<PostResponsesPageDTO> getUserPosts(@AuthenticationPrincipal UserDetails userDetails ,
                                                             @PathVariable Long userId,
                                                             @RequestParam(defaultValue = "1") int page,
                                                             @RequestParam(defaultValue = "10") int size,
                                                             @RequestParam(defaultValue = "createdAt") String sortBy,
                                                             @RequestParam(defaultValue = "desc") String sortDirection){
        return ResponseEntity.ok(postService.getUserPosts(userDetails, userId, page, size, sortBy, sortDirection));
    }

//    @GetMapping("/post/{postId}/comments")
//    public ResponseEntity<PostResponseDTO> getPostComments(@AuthenticationPrincipal UserDetails userDetails ,
//                                                      @PathVariable Long postId,
//                                                      @RequestParam(defaultValue = "1") int page,
//                                                      @RequestParam(defaultValue = "10") int size,
//                                                      @RequestParam(defaultValue = "createdAt") String sortBy,
//                                                      @RequestParam(defaultValue = "desc") String sortDirection){
//        return ResponseEntity.ok(postService.getPostComments(userDetails, postId, page, size, sortBy, sortDirection));
//    }
}
