package com.pixelwave.spring_boot.DTO.comment;

import com.pixelwave.spring_boot.DTO.Image.ImageDTO;
import com.pixelwave.spring_boot.DTO.user.UserResponseDTO;
import com.pixelwave.spring_boot.model.Image;
import com.pixelwave.spring_boot.model.Post;
import com.pixelwave.spring_boot.model.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentResponseDTO {
    private Long id;

    private String content;

    private LocalDateTime createdAt;

    private UserResponseDTO user;

    private List<ImageDTO> images = new ArrayList<>();

}
