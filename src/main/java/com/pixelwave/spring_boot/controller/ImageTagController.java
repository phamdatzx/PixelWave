package com.pixelwave.spring_boot.controller;

import com.pixelwave.spring_boot.DTO.ImageDataDTO;
import com.pixelwave.spring_boot.DTO.ImageTagDTO;
import com.pixelwave.spring_boot.service.ImageTagService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/images")
public class ImageTagController {

    private final ImageTagService imageTagService;

    public ImageTagController(ImageTagService imageTagService) {
        this.imageTagService = imageTagService;
    }

    @PostMapping("/upload")
    public ResponseEntity<ImageDataDTO> uploadImage(@ModelAttribute ImageTagDTO dto) {
        ImageDataDTO result = imageTagService.processImageAndGetTags(dto.getFiles().getFirst());
        return ResponseEntity.ok(result);
    }

    @PostMapping("/process-url")
    public ResponseEntity<ImageDataDTO> processImageFromUrl(@RequestParam("url") String imageUrl) {
        ImageDataDTO result = imageTagService.processImageFromUrlAndGetTags(imageUrl);
        return ResponseEntity.ok(result);
    }
} 