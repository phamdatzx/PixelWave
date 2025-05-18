package com.pixelwave.spring_boot.controller;

import com.pixelwave.spring_boot.DTO.post.CommentRequestDTO;
import com.pixelwave.spring_boot.DTO.post.CommentResponseDTO;
import com.pixelwave.spring_boot.model.User;
import com.pixelwave.spring_boot.service.impl.CommentServiceImpl;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentServiceImpl commentService;

    @PostMapping
    public ResponseEntity<CommentResponseDTO> createComment(
            @RequestBody CommentRequestDTO commentRequestDTO,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(commentService.createComment(commentRequestDTO, ((User) userDetails).getId()));
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<CommentResponseDTO> updateComment(
            @PathVariable Long commentId,
            @RequestBody CommentRequestDTO commentRequestDTO,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(commentService.updateComment(commentId, commentRequestDTO, ((User) userDetails).getId()));
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long commentId,
            @AuthenticationPrincipal UserDetails userDetails) {
        commentService.deleteComment(commentId, ((User) userDetails).getId());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/post/{postId}")
    public ResponseEntity<List<CommentResponseDTO>> getCommentsByPostId(@PathVariable Long postId) {
        return ResponseEntity.ok(commentService.getCommentsByPostId(postId));
    }

    @GetMapping("/{commentId}")
    public ResponseEntity<CommentResponseDTO> getCommentById(@PathVariable Long commentId) {
        return ResponseEntity.ok(commentService.getCommentById(commentId));
    }

    @GetMapping("/{commentId}/replies")
    public ResponseEntity<List<CommentResponseDTO>> getRepliesByCommentId(@PathVariable Long commentId) {
        return ResponseEntity.ok(commentService.getRepliesByCommentId(commentId));
    }
} 