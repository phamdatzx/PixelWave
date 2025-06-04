package com.pixelwave.spring_boot.service;

import com.pixelwave.spring_boot.DTO.ImageDataDTO;
import com.pixelwave.spring_boot.DTO.PostRecommendationDTO;
import com.pixelwave.spring_boot.DTO.Image.ImageDTO;
import com.pixelwave.spring_boot.DTO.post.*;
import com.pixelwave.spring_boot.DTO.user.UserDTO;
import com.pixelwave.spring_boot.exception.ForbiddenException;
import com.pixelwave.spring_boot.exception.ResourceNotFoundException;
import com.pixelwave.spring_boot.model.*;
import com.pixelwave.spring_boot.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PostService {

    private final ModelMapper modelMapper;
    private final PostRepository postRepository;
    private final ImageService imageService;
    private final UserRepository userRepository;
    private final ImageTagService imageTagService;
    private final TagRepository tagRepository;
    private final NotificationService notificationService;
    private final PostViewRepository postViewRepository;
    private final CommentRepository commentRepository;
    private final ImageRepository imageRepository;

    @Value("${post-image-directory}")
    private String postImageDirectory;

    public PostDetailDTO uploadPost(UserDetails userDetails, UploadPostDTO uploadPostDTO) {
        // Get a managed User entity
        User currentUser = userRepository.findById(((User) userDetails).getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        //Get tagged users
        List<User> taggedUsers = userRepository.findAllByIdIn(uploadPostDTO.getTaggedUserIds());

        Post post = Post.builder()
                .caption(uploadPostDTO.getCaption())
                .privacySetting(uploadPostDTO.getPrivacySetting())
                .user(currentUser)
                .taggedUsers(taggedUsers)
                .createdAt(LocalDateTime.now())
                .build();

        post.setImages(imageService.uploadImages(uploadPostDTO.getImages(), postImageDirectory));

        for(Image image : post.getImages()){
            ImageDataDTO res = imageTagService.processImageFromUrlAndGetTags(image.getUrl());
            image.setTags(res.getTags().stream().map(tag -> tagRepository.findByName(tag).orElse(null)).collect(Collectors.toList()));
        }

        var savedPost = postRepository.save(post);

        if(post.getPrivacySetting().equals("public")){
            //trigger notification for all followers
            for (User follower : currentUser.getFollowers()) {
                notificationService.sendNotification(follower, currentUser, NotificationType.NEW_POST, savedPost.getId());
            }
        }

        //trigger notification for tagged users
        for(User taggedUser : taggedUsers) {
            notificationService.sendNotification(taggedUser, currentUser, NotificationType.TAGGED_IN_POST, savedPost.getId());
        }

        return modelMapper.map(savedPost, PostDetailDTO.class);
    }

    public PostDetailDTO getPostById(UserDetails userDetails,Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found: " + postId));

        //case when the user is not logged in
        if(userDetails==null && !post.getPrivacySetting().equals("public")){
            throw new ForbiddenException("You are not allowed to view this post");
        }

        if(userDetails != null) {
            //check if the user is not the post owner
            if(!post.getUser().getId().equals(((User) userDetails).getId())){
                //check privacy setting
                if(post.getPrivacySetting().equals("private")){
                    throw new ForbiddenException("You are not allowed to view this post");
                }
                //check if the post is friend
                if(post.getPrivacySetting().equals("friend")){
                    //check if the user is a friend of the post owner
                    if(!post.getUser().isFriendWith(((User) userDetails).getId())){
                        throw new ForbiddenException("You are not allowed to view this post");
                    }
                }
            }
        }

        PostDetailDTO responseDTO = PostDetailDTO.builder()
                .id(post.getId())
                .caption(post.getCaption())
                .createdAt(Timestamp.valueOf(post.getCreatedAt()))
                .privacySetting(post.getPrivacySetting())
                .postUser(modelMapper.map(post.getUser(), UserDTO.class))
                .likeCount(post.getLikeCount())
                .commentCount(post.getCommentCount())
                .tagUserCount(post.getTaggedUsers().size())
                .isTaggedUser(userDetails != null && post.isTaggedUser((User) userDetails))
                .isLiked(userDetails != null && post.isLikedByUser((User) userDetails))
                .build();

        responseDTO.setImages(post.getImages().stream().map(image -> modelMapper.map(image, ImageDTO.class)).collect(Collectors.toList()));

        return responseDTO;
    }

    @Transactional
    public void likePost(UserDetails userDetails, Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found: " + postId));

        // Check if the user hasn't already liked the post
        User currentUser = (User) userDetails;
        if (!post.isLikedByUser(currentUser)) {
            post.getLikedBy().add(currentUser);
            post.setLikeCount(post.getLikeCount() + 1);
        } 
        postRepository.save(post);
    }

    @Transactional
    public void unlikePost(UserDetails userDetails, Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found: " + postId));

        // Check if the user has already liked the post
        User currentUser = (User) userDetails;

        if (post.isLikedByUser(currentUser)) {
            post.getLikedBy().remove(currentUser);
            post.setLikeCount(post.getLikeCount() - 1);
        }
        postRepository.save(post);
        postRepository.unlikePost(postId, ((User) userDetails).getId());
    }

    public PostSimplePageDTO getUserPosts(UserDetails userDetails, Long userId, int page, int size, String sortBy, String sortDirection) {
        // Validate user existence
        User findUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));

        Sort sort = Sort.by(sortDirection.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC, sortBy);
        Pageable pageable = PageRequest.of(page - 1, size, sort );

        Page<Post> postPage;
        if(userDetails != null){
            if (userId.equals(((User) userDetails).getId())) {
                postPage = postRepository.findByUserId(userId, pageable);
            } else if (findUser.isFriendWith(((User) userDetails).getId())) {
                postPage = postRepository.findByUserIdAndPrivacySettingIn(userId, List.of("friend", "public"), pageable);
            } else {
                postPage = postRepository.findByUserIdAndPrivacySetting(userId, "public", pageable);
            }
        }
        else {
            postPage = postRepository.findByUserIdAndPrivacySetting(userId, "public", pageable);
        }

        List<PostSimpleDTO> postSimpleDTOs = postPage.getContent().stream()
                .map(post -> {
                    PostSimpleDTO postSimpleDTO = PostSimpleDTO.builder().
                            id(post.getId())
                            .likeCount(post.getLikeCount())
                            .commentCount(post.getCommentCount())
                            .build();
                    postSimpleDTO.setImageUrl(post.getImages().isEmpty() ? null : post.getImages().getFirst().getUrl());
                    return postSimpleDTO;
                })
                .toList();

        return PostSimplePageDTO.builder()
                .posts(postSimpleDTOs)
                .totalPages(postPage.getTotalPages())
                .totalElements(postPage.getTotalElements())
                .pageSize(postPage.getSize())
                .currentPage(postPage.getNumber() + 1)
                .build();
    }

    public PostSimplePageDTO getUserTaggedPosts(UserDetails userDetails, Long userId, int page, int size, String sortBy, String sortDirection) {
        // Validate user existence
        User targetuser = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));

        Sort sort = Sort.by(sortDirection.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC, sortBy);
        Pageable pageable = PageRequest.of(page - 1, size, sort );

        List<String> privacySettingList = new ArrayList<>(List.of("public"));
        if(userDetails != null && targetuser.isFriendWith(((User) userDetails).getId())) {
            privacySettingList.add("friend");
        }

        Page<Post> postPage = postRepository.findByTaggedUsersIdAndPrivacySettingIn(userId,privacySettingList, pageable);

        List<PostSimpleDTO> postSimpleDTOs = postPage.getContent().stream()
                .map(post -> {
                    PostSimpleDTO postSimpleDTO = PostSimpleDTO.builder().
                            id(post.getId())
                            .likeCount(post.getLikeCount())
                            .commentCount(post.getCommentCount())
                            .build();
                    postSimpleDTO.setImageUrl(post.getImages().isEmpty() ? null : post.getImages().getFirst().getUrl());
                    return postSimpleDTO;
                })
                .toList();

        return PostSimplePageDTO.builder()
                .posts(postSimpleDTOs)
                .totalPages(postPage.getTotalPages())
                .totalElements(postPage.getTotalElements())
                .pageSize(postPage.getSize())
                .currentPage(postPage.getNumber() + 1)
                .build();
    }


    public List<PostDetailDTO> getFeedPosts(Long queryUserId, boolean isFriend, int limit) {
        User queryUser = userRepository.findById(queryUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + queryUserId));
        List<Object[]> results = postRepository.findPostsWithImages(queryUserId, isFriend, limit);
        
        // Use a Map to group by postId
        Map<Long, PostDetailDTO> postsMap = new HashMap<>();

        for (int i = 0; i < results.size(); i++) {
            Object[] row = results.get(i);
            Long postId = (Long) row[0];

            // Get or create the post DTO
            PostDetailDTO postDTO = postsMap.computeIfAbsent(postId, id -> {
                PostDetailDTO dto = new PostDetailDTO();
                dto.setId(id);
                dto.setCaption((String) row[1]);
                dto.setCreatedAt((Timestamp) row[2]);
                dto.setPrivacySetting((String) row[3]);
                
                UserDTO userDTO = new UserDTO();
                userDTO.setId((Long) row[6]);
                userDTO.setFullName((String) row[7]);
                userDTO.setAvatar((String) row[8]);
                
                dto.setPostUser(userDTO);

                dto.setLikeCount(((Number) row[4]).intValue());
                dto.setCommentCount(((Number) row[5]).intValue());
                dto.setTaggedUser((Boolean) row[10]);
                dto.setTagUserCount(((Number) row[11]).intValue());
                dto.setLiked((Boolean) row[14]);
                
                return dto;
            });
            
            // Add image if not null
            Long imageId = (Long) row[12];
            String imageUrl = (String) row[13];
            
            if (imageId != null && imageUrl != null) {
                ImageDTO imageDTO = new ImageDTO();
                imageDTO.setId(imageId);
                imageDTO.setUrl(imageUrl);
                
                // Check for duplicates before adding
                boolean alreadyExists = postDTO.getImages().stream()
                    .anyMatch(img -> img.getId().equals(imageId));
                    
                if (!alreadyExists) {
                    postDTO.getImages().add(imageDTO);
                }
            }
        }


        // Convert map to list and return
        var res=  new ArrayList<>(postsMap.values());
        List<Long> postIds = new ArrayList<>();
        for (PostDetailDTO post : res) {
            Post postEntity = postRepository.findById(post.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Post not found: " + post.getId()));
            PostView postView = PostView.builder().user(queryUser).post(postEntity).createdAt(LocalDateTime.now()).build();
            System.out.println(postView.getPost().getId());
            System.out.println(postView.getUser().getId());
            postViewRepository.save(postView);
        }


        return res;
    }

    public boolean canAccessPost(Long userId, Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found: " + postId));

        // Check if the user is the post owner
        if (post.getUser().getId().equals(userId)) {
            return true;
        }

        // Check post privacy setting
        switch (post.getPrivacySetting()) {
            case "public":
                return true;
            case "friend":
                // Check if the user is a friend of the post owner
                return post.getUser().isFriendWith(userId);
            case "private":
                return false;
            default:
                return false;
        }
    }

    @Transactional
    public void deletePost(UserDetails userDetails, Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found: " + postId));

        // Check if the user is the post owner
        if (!post.getUser().getId().equals(((User) userDetails).getId())) {
            throw new ForbiddenException("You are not allowed to delete this post");
        }

        for (Image image : post.getImages()) {
            for(Tag tag : image.getTags()) {
                tag.getImages().remove(image);
                tagRepository.save(tag);
            }
            image.getTags().clear();
            imageRepository.delete(image);
        }

        for (Comment comment : post.getComments()) {
            commentRepository.delete(comment);
        }

        // Delete the post
        postRepository.deletePostCompletely(post.getId());
    }

    public List<UserDTO> getTaggedUsers(UserDetails userDetails, Long postId) {
        var getUser = (User) userDetails;

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found: " + postId));

        if( userDetails == null && !post.getPrivacySetting().equals("public")) {
            throw new ForbiddenException("You must be logged in to view tagged users");
        }
        else if(!canAccessPost(getUser.getId(), post.getId())) {
            throw new ForbiddenException("You must be logged in to view tagged users");
        }

        return post.getTaggedUsers().stream().
                map(user -> UserDTO.builder()
                        .id(user.getId())
                        .fullName(user.getFullName())
                        .avatar(user.getAvatar())
                        .build())
                .collect(Collectors.toList());
    }
}