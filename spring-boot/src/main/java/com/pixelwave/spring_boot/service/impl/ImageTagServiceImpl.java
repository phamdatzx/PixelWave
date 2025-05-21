package com.pixelwave.spring_boot.service.impl;

import com.pixelwave.spring_boot.DTO.ImageDataDTO;
import com.pixelwave.spring_boot.service.ImageTagService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.List;

@Service
public class ImageTagServiceImpl implements ImageTagService {

    private final RestTemplate restTemplate;
    private final String pythonServiceUrl;
    private static final List<String> ALLOWED_IMAGE_TYPES = Arrays.asList(
            "image/jpeg", "image/png", "image/gif", "image/webp"
    );

    public ImageTagServiceImpl(
            RestTemplate restTemplate,
            @Value("${python.service.url:http://localhost:8000}") String pythonServiceUrl) {
        this.restTemplate = restTemplate;
        this.pythonServiceUrl = pythonServiceUrl;
    }

    @Override
    public ImageDataDTO processImageAndGetTags(MultipartFile file) {
        try {
            // Create headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            // Create the request body
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", new MultipartFileResource(file));

            // Create the request entity
            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            // Make the request to Python service
            ResponseEntity<ImageDataDTO> response = restTemplate.exchange(
                    pythonServiceUrl + "/upload/",
                    HttpMethod.POST,
                    requestEntity,
                    ImageDataDTO.class
            );

            return response.getBody();
        } catch (IOException e) {
            throw new RuntimeException("Failed to process image", e);
        }
    }

    @Override
    public ImageDataDTO processImageFromUrlAndGetTags(String imageUrl) {
        try {
            // Download image from URL
            URL url = new URL(imageUrl);
            URLConnection connection = url.openConnection();
            
            // Validate content type
            String contentType = connection.getContentType();
            if (contentType == null || !ALLOWED_IMAGE_TYPES.contains(contentType.toLowerCase())) {
                throw new IllegalArgumentException("Invalid image type. Allowed types: " + ALLOWED_IMAGE_TYPES);
            }

            // Get filename from URL
            final String filename = imageUrl.substring(imageUrl.lastIndexOf('/') + 1).isEmpty() 
                ? "downloaded_image" + getFileExtension(contentType)
                : imageUrl.substring(imageUrl.lastIndexOf('/') + 1);

            // Download image bytes
            byte[] imageBytes = connection.getInputStream().readAllBytes();

            // Create headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            // Create the request body
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", new ByteArrayResource(imageBytes) {
                @Override
                public String getFilename() {
                    return filename;
                }
            });

            // Create the request entity
            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            // Make the request to Python service
            ResponseEntity<ImageDataDTO> response = restTemplate.exchange(
                    pythonServiceUrl + "/upload/",
                    HttpMethod.POST,
                    requestEntity,
                    ImageDataDTO.class
            );

            return response.getBody();
        } catch (IOException e) {
            throw new RuntimeException("Failed to process image from URL: " + imageUrl, e);
        }
    }

    private String getFileExtension(String contentType) {
        return switch (contentType.toLowerCase()) {
            case "image/jpeg" -> ".jpg";
            case "image/png" -> ".png";
            case "image/gif" -> ".gif";
            case "image/webp" -> ".webp";
            default -> ".img";
        };
    }

    private static class MultipartFileResource extends ByteArrayResource {
        private final String filename;

        public MultipartFileResource(MultipartFile file) throws IOException {
            super(file.getBytes());
            this.filename = file.getOriginalFilename();
        }

        @Override
        public String getFilename() {
            return filename;
        }
    }
} 