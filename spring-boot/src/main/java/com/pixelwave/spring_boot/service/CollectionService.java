package com.pixelwave.spring_boot.service;

import com.pixelwave.spring_boot.DTO.collection.CollectionRequestDTO;
import com.pixelwave.spring_boot.DTO.collection.CollectionResponseDTO;
import com.pixelwave.spring_boot.DTO.post.PostSimpleDTO;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

public interface CollectionService {
    CollectionResponseDTO createCollection(CollectionRequestDTO collectionRequestDTO, UserDetails userDetails);
    CollectionResponseDTO updateCollection(Long collectionId, CollectionRequestDTO collectionRequestDTO, UserDetails userDetails);
    void deleteCollection(Long collectionId, UserDetails userDetails);
    CollectionResponseDTO getCollectionById(Long collectionId, UserDetails userDetails);
    List<CollectionResponseDTO> getUserCollections(UserDetails userDetails);
    void addPostToCollection(Long collectionId, Long postId, UserDetails userDetails);
    void removePostFromCollection(Long collectionId, Long postId, UserDetails userDetails);
    List<PostSimpleDTO> getPostsInCollection(Long collectionId, String search, UserDetails userDetails);
} 