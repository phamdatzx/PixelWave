package com.pixelwave.spring_boot.controller;

import com.pixelwave.spring_boot.DTO.collection.CollectionRequestDTO;
import com.pixelwave.spring_boot.DTO.collection.CollectionResponseDTO;
import com.pixelwave.spring_boot.DTO.post.PostSimpleDTO;
import com.pixelwave.spring_boot.model.User;
import com.pixelwave.spring_boot.service.CollectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/collections")
@RequiredArgsConstructor
public class CollectionController {

    private final CollectionService collectionService;

    @PostMapping
    public ResponseEntity<CollectionResponseDTO> createCollection(
            @RequestBody CollectionRequestDTO collectionRequestDTO,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(collectionService.createCollection(collectionRequestDTO, userDetails));
    }

    @PutMapping("/{collectionId}")
    public ResponseEntity<CollectionResponseDTO> updateCollection(
            @PathVariable Long collectionId,
            @RequestBody CollectionRequestDTO collectionRequestDTO,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(collectionService.updateCollection(collectionId, collectionRequestDTO, userDetails));
    }

    @DeleteMapping("/{collectionId}")
    public ResponseEntity<Void> deleteCollection(
            @PathVariable Long collectionId,
            @AuthenticationPrincipal UserDetails userDetails) {
        collectionService.deleteCollection(collectionId, userDetails);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{collectionId}")
    public ResponseEntity<CollectionResponseDTO> getCollectionById(
            @PathVariable Long collectionId,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(collectionService.getCollectionById(collectionId, userDetails));
    }

    @GetMapping("/user")
    public ResponseEntity<List<CollectionResponseDTO>> getUserCollections(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(collectionService.getUserCollections(userDetails));
    }

    @PostMapping("/{collectionId}/posts/{postId}")
    public ResponseEntity<Void> addPostToCollection(
            @PathVariable Long collectionId,
            @PathVariable Long postId,
            @AuthenticationPrincipal UserDetails userDetails) {
        collectionService.addPostToCollection(collectionId, postId, userDetails);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{collectionId}/posts/{postId}")
    public ResponseEntity<Void> removePostFromCollection(
            @PathVariable Long collectionId,
            @PathVariable Long postId,
            @AuthenticationPrincipal UserDetails userDetails) {
        collectionService.removePostFromCollection(collectionId, postId, userDetails);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{collectionId}/posts")
    public ResponseEntity<List<PostSimpleDTO>> getPostsInCollection(
            @PathVariable Long collectionId,
            @RequestParam(required = false) String search,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(collectionService.getPostsInCollection(collectionId, search, userDetails));
    }
} 