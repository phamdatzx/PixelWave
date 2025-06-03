package com.pixelwave.spring_boot.service.impl;

import com.google.api.client.util.Value;
import com.pixelwave.spring_boot.DTO.post.CommentRequestDTO;
import com.pixelwave.spring_boot.DTO.post.CommentResponseDTO;
import com.pixelwave.spring_boot.DTO.post.ImageDTO;
import com.pixelwave.spring_boot.DTO.post.UserDTO;
import com.pixelwave.spring_boot.exception.ForbiddenException;
import com.pixelwave.spring_boot.exception.ResourceNotFoundException;
import com.pixelwave.spring_boot.model.*;
import com.pixelwave.spring_boot.repository.CommentRepository;
import com.pixelwave.spring_boot.repository.PostRepository;
import com.pixelwave.spring_boot.repository.UserRepository;
import com.pixelwave.spring_boot.service.ImageService;
import com.pixelwave.spring_boot.service.NotificationService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final ImageService imageService;
    private final NotificationService notificationService;

    @Value("${comment-image-directory}")
    private String commentImageDirectory;

    @Transactional
    public CommentResponseDTO createComment(CommentRequestDTO commentRequestDTO, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Post post = postRepository.findById(commentRequestDTO.getPostId())
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));

        //create comment and save to db
        Comment comment = Comment.builder()
                .content(commentRequestDTO.getContent())
                .user(user)
                .post(post)
                .createdAt(LocalDateTime.now())
                .build();

        if (commentRequestDTO.getParentCommentId() != null) {
            Comment parentComment = commentRepository.findById(commentRequestDTO.getParentCommentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Parent comment not found"));
            comment.setParent(parentComment);
            if(parentComment.getReplies() == null) {
                parentComment.setReplies(new ArrayList<>());
            }
            parentComment.getReplies().add(comment);
        }

        //upload images
        if(commentRequestDTO.getImages() != null) {
            List<Image> images = imageService.uploadImages(commentRequestDTO.getImages(), commentImageDirectory);
            for(Image image : images) {
                image.setComment(comment);
            }
            comment.setImages(images);
        }
        
        Comment savedComment = commentRepository.save(comment);
        post.setCommentCount(post.getCommentCount() + 1);
        postRepository.save(post);

        //trigger notification
        //notify the owner of the comment be replied to if exists
        if(commentRequestDTO.getParentCommentId() != null) {
            var parrentComment = commentRepository.findById(commentRequestDTO.getParentCommentId()).orElseThrow(() -> new ResourceNotFoundException("Parent comment not found"));
            notificationService.sendNotification(parrentComment.getUser(), user, NotificationType.REPLY_TO_COMMENT,savedComment.getId());
        }
        //notify the owner of the post
        notificationService.sendNotification(post.getUser(), user, NotificationType.NEW_COMMENT, savedComment.getId());

        return mapToCommentResponseDTO(savedComment);
    }

    @Transactional
    public CommentResponseDTO updateComment(Long commentId, CommentRequestDTO commentRequestDTO, Long userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found"));

        if (!comment.getUser().getId().equals(userId)) {
            throw new IllegalStateException("User is not authorized to update this comment");
        }

        comment.setContent(commentRequestDTO.getContent());
        Comment updatedComment = commentRepository.save(comment);

        //upload images
        if(commentRequestDTO.getImages() != null) {
                    List<Image> images = imageService.uploadImages(commentRequestDTO.getImages(), commentImageDirectory);
                    for(Image image : images) {
                        image.setComment(comment);
                    }
                    comment.setImages(images);
        }

        return mapToCommentResponseDTO(updatedComment);
    }

    @Transactional
    public void deleteComment(Long commentId, Long userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found"));

        if (!comment.getUser().getId().equals(userId)) {
            throw new ForbiddenException("User is not authorized to delete this comment");
        }

        Post post = comment.getPost();
        // Calculate total comments to be deleted (including all nested replies)
        int totalCommentsToDelete = countTotalComments(comment);
        post.setCommentCount(post.getCommentCount() - totalCommentsToDelete);
        postRepository.save(post);

        commentRepository.delete(comment);
    }

    private int countTotalComments(Comment comment) {
        int count = 1; // Count the current comment
        for (Comment reply : comment.getReplies()) {
            count += countTotalComments(reply); // Recursively count nested replies
        }
        return count;
    }

    public List<CommentResponseDTO> getCommentsByPostId(Long postId) {
        List<Comment> comments = commentRepository.findByPostIdAndParentIsNull(postId);
        return comments.stream()
                .map(this::mapToCommentResponseDTO)
                .collect(Collectors.toList());
    }

    public List<CommentResponseDTO> getRepliesByCommentId(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found"));
        return comment.getReplies().stream()
                .map(this::mapToCommentResponseDTO)
                .collect(Collectors.toList());
    }

    public CommentResponseDTO getCommentById(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found"));
        return mapToCommentResponseDTO(comment);
    }

    private CommentResponseDTO mapToCommentResponseDTO(Comment comment) {
        return CommentResponseDTO.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .user(UserDTO.builder()
                        .id(comment.getUser().getId())
                        .fullName(comment.getUser().getFullName())
                        .avatar(comment.getUser().getAvatar())
                        .build())
                .images(comment.getImages() != null ? comment.getImages().stream()
                        .map(image -> ImageDTO.builder()
                                .id(image.getId())
                                .url(image.getUrl())
                                .build())
                        .collect(Collectors.toList()) : List.of())
                .hasReplies(comment.getReplies() != null && !comment.getReplies().isEmpty())
                .build();
    }
} 