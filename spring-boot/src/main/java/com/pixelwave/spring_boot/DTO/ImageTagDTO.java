package com.pixelwave.spring_boot.DTO;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class ImageTagDTO {
    private List<MultipartFile> files;
}
