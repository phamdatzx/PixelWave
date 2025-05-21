package com.pixelwave.spring_boot.DTO;

import lombok.Data;
import java.util.List;

@Data
public class ImageDataDTO {
    private String filename;
    private List<String> tags;
} 