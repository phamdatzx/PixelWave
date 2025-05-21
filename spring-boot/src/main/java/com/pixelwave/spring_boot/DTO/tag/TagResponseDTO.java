package com.pixelwave.spring_boot.DTO.tag;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TagResponseDTO {
    private Long id;
    private String name;
    private int imageCount;
} 