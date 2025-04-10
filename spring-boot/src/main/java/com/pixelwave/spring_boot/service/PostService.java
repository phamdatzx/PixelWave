package com.pixelwave.spring_boot.service;

import com.pixelwave.spring_boot.DTO.post.PostResponseDTO;
import com.pixelwave.spring_boot.DTO.post.PostResponseWithoutUserDTO;
import com.pixelwave.spring_boot.DTO.post.PostResponsesPageDTO;
import com.pixelwave.spring_boot.DTO.post.UploadPostDTO;
import com.pixelwave.spring_boot.exception.ResourceNotFoundException;
import com.pixelwave.spring_boot.model.Post;
import com.pixelwave.spring_boot.model.User;
import com.pixelwave.spring_boot.repository.PostRepository;
import com.pixelwave.spring_boot.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

    private final ModelMapper modelMapper;
    private final PostRepository postRepository;
    private final ImageService imageService;
    private final UserRepository userRepository;

    public PostResponseDTO uploadPost(UserDetails userDetails, UploadPostDTO uploadPostDTO) {
        // Get a managed User entity
        User currentUser = userRepository.findById(((User) userDetails).getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        //Get tagged users
        List<User> taggedUsers = userRepository.findAllByIdIn(uploadPostDTO.getTaggedUserIds());

        // Create the Post entity without saving it yet
        Post post = Post.builder()
                .caption(uploadPostDTO.getCaption())
                .privacySetting(uploadPostDTO.getPrivacySetting())
                .user(currentUser)
                .taggedUsers(taggedUsers)
                .createdAt(LocalDateTime.now())
                .build();

        post.setImages(imageService.uploadImages(uploadPostDTO.getImages()));

        return modelMapper.map(postRepository.save(post), PostResponseDTO.class);
    }

    public PostResponseDTO getPostById(UserDetails userDetails,Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found: " + postId));
        //check if the user is the post owner
        if(!post.getUser().getId().equals(((User) userDetails).getId())){
            //check privacy setting
            if(post.getPrivacySetting().equals("private")){
                throw new ResourceNotFoundException("Post not found: " + postId);
            }
            //check if the post is friend
            if(post.getPrivacySetting().equals("friend")){
                //check if the user is a friend of the post owner
                if(!post.getUser().isFriendWith(((User) userDetails).getId())){
                    throw new ResourceNotFoundException("Post not found: " + postId);
                }
            }
        }

        var responseDTO = modelMapper.map(post, PostResponseDTO.class);

        //get like status
        if(post.isLikedByUser((User) userDetails)){
            responseDTO.setLikedByUser(true);
        }

        return responseDTO;
    }

    public void toggleLikePost(UserDetails userDetails, Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found: " + postId));

        // Check if the user has already liked the post
        User currentUser = (User) userDetails;
        if (post.isLikedByUser(currentUser)) {
            post.getLikedBy().remove(currentUser);
            post.setLikeCount(post.getLikeCount() - 1);
        } else {
            post.getLikedBy().add(currentUser);
            post.setLikeCount(post.getLikeCount() + 1);
        }

        postRepository.save(post);
    }

    public PostResponsesPageDTO getUserPosts(UserDetails userDetails, Long userId, int page, int size, String sortBy, String sortDirection) {
        // Validate user existence
        User findUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));

        Sort sort = Sort.by(sortDirection.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC, sortBy);
        Pageable pageable = PageRequest.of(page - 1, size, sort );

        Page<Post> postPage;
        if (userId.equals(((User) userDetails).getId())) {
            postPage = postRepository.findByUserId(userId, pageable);
        } else if (findUser.isFriendWith(((User) userDetails).getId())) {
            postPage = postRepository.findByUserIdAndPrivacySettingIn(userId, List.of("friend", "public"), pageable);
        } else {
            postPage = postRepository.findByUserIdAndPrivacySetting(userId, "public", pageable);
        }

        // Map posts to PostResponseDTO
        List<PostResponseWithoutUserDTO> postResponseDTOs = postPage.getContent().stream()
                .map(post -> {
                    PostResponseWithoutUserDTO responseDTO = modelMapper.map(post, PostResponseWithoutUserDTO.class);
                    // Set like status for the current user
                    responseDTO.setLikedByUser(post.getLikedBy().stream()
                            .anyMatch(user -> user.getId().equals(((User) userDetails).getId())));
                    return responseDTO;
                })
                .toList();

        // Build and return the paginated response
        return PostResponsesPageDTO.builder()
                .posts(postResponseDTOs)
                .currentPage(postPage.getNumber() + 1)
                .totalPages(postPage.getTotalPages())
                .totalElements(postPage.getTotalElements())
                .pageSize(postPage.getSize())
                .build();

    }


}