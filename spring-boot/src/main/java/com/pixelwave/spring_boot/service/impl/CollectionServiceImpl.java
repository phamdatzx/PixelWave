package com.pixelwave.spring_boot.service.impl;

import com.pixelwave.spring_boot.DTO.collection.CollectionRequestDTO;
import com.pixelwave.spring_boot.DTO.collection.CollectionResponseDTO;
import com.pixelwave.spring_boot.DTO.post.PostSimpleDTO;
import com.pixelwave.spring_boot.DTO.user.UserDTO;
import com.pixelwave.spring_boot.exception.ForbiddenException;
import com.pixelwave.spring_boot.exception.ResourceNotFoundException;
import com.pixelwave.spring_boot.model.Collection;
import com.pixelwave.spring_boot.model.Post;
import com.pixelwave.spring_boot.model.User;
import com.pixelwave.spring_boot.repository.CollectionRepository;
import com.pixelwave.spring_boot.repository.PostRepository;
import com.pixelwave.spring_boot.repository.UserRepository;
import com.pixelwave.spring_boot.service.CollectionService;
import com.pixelwave.spring_boot.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CollectionServiceImpl implements CollectionService {

    private final CollectionRepository collectionRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final PostService postService;

    @Override
    @Transactional
    public CollectionResponseDTO createCollection(CollectionRequestDTO collectionRequestDTO, UserDetails userDetails) {
        User user = (User) userDetails;
        Collection collection = Collection.builder()
                .title(collectionRequestDTO.getTitle())
                .description(collectionRequestDTO.getDescription())
                .isPublic(collectionRequestDTO.getIsPublic())
                .user(user)
                .build();

        Collection savedCollection = collectionRepository.save(collection);
        return mapToCollectionResponseDTO(savedCollection);
    }

    @Override
    @Transactional
    public CollectionResponseDTO updateCollection(Long collectionId, CollectionRequestDTO collectionRequestDTO, UserDetails userDetails) {
        Collection collection = collectionRepository.findById(collectionId)
                .orElseThrow(() -> new ResourceNotFoundException("Collection not found"));

        if (!collection.getUser().getId().equals(((User) userDetails).getId())) {
            throw new ForbiddenException("User is not authorized to update this collection");
        }

        collection.setTitle(collectionRequestDTO.getTitle());
        collection.setDescription(collectionRequestDTO.getDescription());
        collection.setIsPublic(collectionRequestDTO.getIsPublic());

        Collection updatedCollection = collectionRepository.save(collection);
        return mapToCollectionResponseDTO(updatedCollection);
    }

    @Override
    @Transactional
    public void deleteCollection(Long collectionId, UserDetails userDetails) {
        Collection collection = collectionRepository.findById(collectionId)
                .orElseThrow(() -> new ResourceNotFoundException("Collection not found"));

        if (!collection.getUser().getId().equals(((User) userDetails).getId())) {
            throw new ForbiddenException("User is not authorized to delete this collection");
        }

        collectionRepository.delete(collection);
    }

    @Override
    public CollectionResponseDTO getCollectionById(Long collectionId, UserDetails userDetails) {
        Collection collection = collectionRepository.findById(collectionId)
                .orElseThrow(() -> new ResourceNotFoundException("Collection not found"));

        if (!collection.getIsPublic() && !collection.getUser().getId().equals(((User) userDetails).getId())) {
            throw new ForbiddenException("This collection is private");
        }

        return mapToCollectionResponseDTO(collection);
    }

    @Override
    public List<CollectionResponseDTO> getUserCollections(UserDetails userDetails) {
        User user = (User) userDetails;
        List<Collection> collections = collectionRepository.findByUserId(user.getId());
        return collections.stream()
                .map(this::mapToCollectionResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void addPostToCollection(Long collectionId, Long postId, UserDetails userDetails) {
        Collection collection = collectionRepository.findById(collectionId)
                .orElseThrow(() -> new ResourceNotFoundException("Collection not found"));

        if (!collection.getUser().getId().equals(((User) userDetails).getId())) {
            throw new ForbiddenException("User is not authorized to modify this collection");
        }

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));

        if (!collection.getPosts().contains(post)) {
            collection.getPosts().add(post);
            collectionRepository.save(collection);
        }
    }

    @Override
    @Transactional
    public void removePostFromCollection(Long collectionId, Long postId, UserDetails userDetails) {
        Collection collection = collectionRepository.findById(collectionId)
                .orElseThrow(() -> new ResourceNotFoundException("Collection not found"));

        if (!collection.getUser().getId().equals(((User) userDetails).getId())) {
            throw new ForbiddenException("User is not authorized to modify this collection");
        }

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));

        collection.getPosts().remove(post);
        collectionRepository.save(collection);
    }

    @Override
    public List<PostSimpleDTO> getPostsInCollection(Long collectionId, String search, UserDetails userDetails) {
        Collection collection = collectionRepository.findById(collectionId)
                .orElseThrow(() -> new ResourceNotFoundException("Collection not found"));

        // Check if the user can access the collection
        if (!collection.getIsPublic() && !collection.getUser().getId().equals(((User) userDetails).getId())) {
            throw new ForbiddenException("You don't have permission to access this collection");
        }

        // Get all posts that user has access to
        List<Post> accessiblePosts = collection.getPosts().stream()
                .filter(post -> postService.canAccessPost(((User) userDetails).getId(), post.getId()))
                .collect(Collectors.toList());

        // Apply search filter if search term is provided
        if (search != null && !search.trim().isEmpty()) {
            String searchLower = search.toLowerCase();
            accessiblePosts = accessiblePosts.stream()
                    .filter(post -> post.getCaption() != null && 
                            post.getCaption().toLowerCase().contains(searchLower))
                    .collect(Collectors.toList());
        }

        // Convert to DTOs
        return accessiblePosts.stream()
                .map(post -> PostSimpleDTO.builder()
                        .id(post.getId())
                        .imageUrl(post.getImages().isEmpty() ? null : post.getImages().get(0).getUrl())
                        .likeCount(post.getLikeCount())
                        .commentCount(post.getCommentCount())
                        .build())
                .collect(Collectors.toList());
    }

    private CollectionResponseDTO mapToCollectionResponseDTO(Collection collection) {
        return CollectionResponseDTO.builder()
                .id(collection.getId())
                .title(collection.getTitle())
                .description(collection.getDescription())
                .isPublic(collection.getIsPublic())
                .user(UserDTO.builder()
                        .id(collection.getUser().getId())
                        .fullName(collection.getUser().getFullName())
                        .avatar(collection.getUser().getAvatar())
                        .build())
                .postCount(collection.getPosts().size())
                .build();
    }
} 