package com.pixelwave.spring_boot.DTO.collection;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CollectionRequestDTO {
    private String title;
    private String description;
    private Boolean isPublic;
} 