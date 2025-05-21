package com.pixelwave.spring_boot.DTO;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class UploadImageDTO {
    @NotEmpty(message = "At least one image is required")
    private MultipartFile file;

    private String test;
}
