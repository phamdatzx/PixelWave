package com.pixelwave.spring_boot.DTO.Image;

import lombok.Builder;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@Builder
public class ImageMessageDTO {
    private List<MultipartFile> images;
}
