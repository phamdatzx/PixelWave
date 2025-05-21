package com.pixelwave.spring_boot;

import com.pixelwave.spring_boot.DTO.ImageDataDTO;
import com.pixelwave.spring_boot.model.Image;
import com.pixelwave.spring_boot.model.Tag;
import com.pixelwave.spring_boot.repository.ImageRepository;
import com.pixelwave.spring_boot.repository.TagRepository;
import com.pixelwave.spring_boot.service.ImageTagService;

import lombok.RequiredArgsConstructor;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.stream.Collectors;

@SpringBootApplication
@RequiredArgsConstructor
public class Application {

	private final TagRepository tagRepository;
	private final ImageTagService imageTagService;
	private final ImageRepository imageRepository;

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Bean
	public CommandLineRunner commandLineRunner() {
		return args -> {
//			for( long i = 241; i < 264; i++){
//				try{
//					Image image = imageRepository.findById(i).orElse(null);
//					if(image != null){
//							ImageDataDTO res = imageTagService.processImageFromUrlAndGetTags(image.getUrl());
//							image.setTags(res.getTags().stream().map(tag -> tagRepository.findByName(tag).orElse(null)).collect(Collectors.toList()));
//							imageRepository.save(image);
//					}
//					else{
//						System.out.println("Image not found " + i);
//					}
//
//				}
//				catch(Exception e){
//					System.out.println("Error processing image " + i);
//				}
//
//			}
		};
	}
}
