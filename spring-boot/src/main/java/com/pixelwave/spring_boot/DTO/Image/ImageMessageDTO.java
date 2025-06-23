package com.pixelwave.spring_boot.DTO.Image;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@Builder
public class ImageMessageDTO {
    @NotEmpty(message = "At least one image is required")
    private List<MultipartFile> images;
}
