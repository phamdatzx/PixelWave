package com.pixelwave.spring_boot.DTO.post;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;

@Data
public class UploadPostDTO {
    private String caption;

    @NotBlank(message = "Privacy setting is required")
    @Pattern(regexp = "^(friend|private|public)$", message = "Privacy setting must be 'friend', 'private', or 'public'")
    private String privacySetting;

    @NotEmpty(message = "At least one image is required")
    private List<MultipartFile> images;

    private List<Long> taggedUserIds;
}
