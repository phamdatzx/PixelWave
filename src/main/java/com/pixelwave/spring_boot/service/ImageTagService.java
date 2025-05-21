package com.pixelwave.spring_boot.service;

import com.pixelwave.spring_boot.DTO.ImageDataDTO;
import org.springframework.web.multipart.MultipartFile;

public interface ImageTagService {
    ImageDataDTO processImageAndGetTags(MultipartFile file);
    ImageDataDTO processImageFromUrlAndGetTags(String imageUrl);
} 