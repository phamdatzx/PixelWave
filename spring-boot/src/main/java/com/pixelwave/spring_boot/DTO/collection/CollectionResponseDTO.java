package com.pixelwave.spring_boot.DTO.collection;

import com.pixelwave.spring_boot.DTO.user.UserDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CollectionResponseDTO {
    private Long id;
    private String title;
    private String description;
    private Boolean isPublic;
    private UserDTO user;
    private Integer postCount;
} 