package com.pixelwave.spring_boot.DTO.Image;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Data;

@Data
public class ImageDTO {

    private Long id;

    private Long size;

    private String url;
}
