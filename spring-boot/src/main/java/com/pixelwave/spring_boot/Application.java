package com.pixelwave.spring_boot;

import com.pixelwave.spring_boot.model.Image;
import com.pixelwave.spring_boot.model.Post;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;
import java.util.stream.Collectors;

@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Bean
	public CommandLineRunner commandLineRunner() {
		return args -> {
			// This is where you can add any startup logic if needed
			System.out.println("Application started successfully!");

			Image image1 = Image.builder()
					.id(1L)
					.size(1024L)
					.url("http://example.com/image1.jpg")
					.build();
			Image image2 = Image.builder()
					.id(2L)
					.size(2048L)
					.url("http://example.com/image2.jpg")
					.build();
			Set<Image> images = Set.of(image1, image2);

			Post post = new Post();

			images.stream()
					.map(image -> uploadImage( post))
					.collect(Collectors.toSet());

			System.out.println(post.getImages());
		};
	}

	public Image uploadImage( Post post) {

		return Image.builder()
				.post(post)
				.build();
	}

}
