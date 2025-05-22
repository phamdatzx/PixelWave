package com.pixelwave.spring_boot.controller;

import com.pixelwave.spring_boot.DTO.ImageDataDTO;
import com.pixelwave.spring_boot.DTO.ImageTagDTO;
import com.pixelwave.spring_boot.DTO.tag.TagImageResponseDTO;
import com.pixelwave.spring_boot.DTO.tag.TagResponseDTO;
import com.pixelwave.spring_boot.service.ImageTagService;
import com.pixelwave.spring_boot.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/images")
@RequiredArgsConstructor
public class ImageTagController {

    private final ImageTagService imageTagService;
    private final TagService tagService;

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

    @GetMapping("/tags")
    public ResponseEntity<List<TagResponseDTO>> getTags(
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(tagService.getTagsSortedByImageCount(limit));
    }

    @GetMapping("/tags/{tagId}/images")
    public ResponseEntity<Page<TagImageResponseDTO>> getImagesByTag(
            @PathVariable Long tagId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageRequest pageRequest = PageRequest.of(page - 1, size, Sort.by("likeCount").descending());
        return ResponseEntity.ok(tagService.getImagesByTagId(tagId, pageRequest));
    }

    @GetMapping("/all-tags")
    public ResponseEntity<List<TagResponseDTO>> getAllTags() {
        return ResponseEntity.ok(tagService.getAllTagsNoSorting());
    }
} 