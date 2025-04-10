package com.pixelwave.spring_boot.DTO.post;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostResponsesPageDTO {
    List<PostResponseWithoutUserDTO> posts;
    int totalPages;
    long totalElements;
    int pageSize;
    int currentPage;
}
