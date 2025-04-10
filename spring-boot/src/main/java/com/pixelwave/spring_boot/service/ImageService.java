package com.pixelwave.spring_boot.service;

import com.pixelwave.spring_boot.DTO.Image.ImageDTO;
import com.pixelwave.spring_boot.exception.ValidationException;
import com.pixelwave.spring_boot.model.Image;
import com.pixelwave.spring_boot.model.Post;
import com.pixelwave.spring_boot.repository.ImageRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ImageService {

    private final ImageRepository imageRepository;
    private final S3Service s3Service;
    private final ModelMapper modelMapper;

    public Image uploadImage(MultipartFile file) {
        //validate image
        validateImage(file);
        //save image to s3
        //String url = s3Service.uploadFile(file, null);
        return
                Image.builder()
                .size(file.getSize())
                .url("url")
                .build();
    }

    public void validateImage(MultipartFile file) {
        // Check if file is empty
        if (file.isEmpty()) {
            throw new ValidationException("File cannot be empty");
        }

        // Validate file is an image
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new ValidationException("Only image files are allowed");
        }

        // Further validation - check file extension
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            throw new ValidationException("Filename is required");
        }

        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf('.') + 1).toLowerCase();
        if (!Arrays.asList("jpg", "jpeg", "png", "gif", "bmp").contains(fileExtension)) {
            throw new ValidationException("Only JPG, JPEG, PNG, GIF and BMP files are allowed");
        }
    }

    public void validateImages(MultipartFile[] files) {
        for (MultipartFile file : files) {
            validateImage(file);
        }
    }

    @Transactional
    public List<Image> uploadImages(List<MultipartFile> images) {
        return images.stream().map(this::uploadImage).collect(Collectors.toList());
    }
}
