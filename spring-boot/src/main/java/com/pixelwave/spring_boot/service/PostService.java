package com.pixelwave.spring_boot.service;

import com.pixelwave.spring_boot.DTO.post.PostResponseDTO;
import com.pixelwave.spring_boot.DTO.post.UploadPostDTO;
import com.pixelwave.spring_boot.exception.ResourceNotFoundException;
import com.pixelwave.spring_boot.model.Post;
import com.pixelwave.spring_boot.model.User;
import com.pixelwave.spring_boot.repository.PostRepository;
import com.pixelwave.spring_boot.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
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

//    public PostResponseDTO getPostById(UserDetails userDetails,Long postId) {
//        Post post = postRepository.findById(postId)
//                .orElseThrow(() -> new ResourceNotFoundException("Post not found: " + postId));
//        //check privacy setting
//        if()
//
//        return modelMapper.map(post, PostResponseDTO.class);
//    }
}