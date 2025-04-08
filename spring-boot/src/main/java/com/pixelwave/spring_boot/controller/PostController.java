package com.pixelwave.spring_boot.controller;

import com.pixelwave.spring_boot.DTO.post.PostResponseDTO;
import com.pixelwave.spring_boot.DTO.post.UploadPostDTO;
import com.pixelwave.spring_boot.service.PostService;
import com.pixelwave.spring_boot.service.S3Service;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
}
