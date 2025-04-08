package com.pixelwave.spring_boot.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

@Service
public class S3Service {

    private final AmazonS3 amazonS3;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    @Autowired
    public S3Service(AmazonS3 amazonS3) {
        this.amazonS3 = amazonS3;
    }

    /**
     * Uploads a file to Amazon S3
     *
     * @param file The file to upload
     * @param directory Optional directory within bucket (can be null)
     * @return The URL of the uploaded file
     * @throws IOException If there's an error reading the file
     */
    public String uploadFile(MultipartFile file, String directory) {
        // Generate a unique file name to avoid collisions
        String fileName = generateUniqueFileName(Objects.requireNonNull(file.getOriginalFilename()));

        // Construct the key (path in S3)
        String key = directory != null ? directory + "/" + fileName : fileName;

        // Set metadata for the file
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        metadata.setContentType(file.getContentType());

        // Upload the file to S3
        try{
            amazonS3.putObject(new PutObjectRequest(
                bucketName,
                key,
                file.getInputStream(),
                metadata
        ));} catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Return the URL of the uploaded file
        return amazonS3.getUrl(bucketName, key).toString();
    }

    /**
     * Deletes a file from Amazon S3
     *
     * @param fileUrl The full URL of the file to delete
     * @return true if deletion was successful, false otherwise
     */
    public boolean deleteFile(String fileUrl) {
        try {
            // Extract the key from the file URL
            String key = extractKeyFromUrl(fileUrl);

            // Delete the file from S3
            amazonS3.deleteObject(new DeleteObjectRequest(bucketName, key));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Alternative delete method that takes the key directly
     *
     * @param key The key (path) of the file in S3
     * @return true if deletion was successful, false otherwise
     */
    public boolean deleteFileByKey(String key) {
        try {
            amazonS3.deleteObject(new DeleteObjectRequest(bucketName, key));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Generates a unique file name to avoid overwriting existing files
     */
    private String generateUniqueFileName(String originalFileName) {
        String extension = "";
        if (originalFileName.contains(".")) {
            extension = originalFileName.substring(originalFileName.lastIndexOf("."));
            originalFileName = originalFileName.substring(0, originalFileName.lastIndexOf("."));
        }
        return originalFileName + "-" + UUID.randomUUID().toString() + extension;
    }

    /**
     * Extracts the key (path) from a full S3 URL
     */
    private String extractKeyFromUrl(String fileUrl) {
        // Remove the base URL part to get the key
        String bucketUrl = "https://" + bucketName + ".s3.amazonaws.com/";
        if (fileUrl.startsWith(bucketUrl)) {
            return fileUrl.substring(bucketUrl.length());
        }

        // Alternative URL format
        String altBucketUrl = "https://s3.amazonaws.com/" + bucketName + "/";
        if (fileUrl.startsWith(altBucketUrl)) {
            return fileUrl.substring(altBucketUrl.length());
        }

        // If URL format is not recognized, return the full URL
        // (this will likely fail when trying to delete)
        return fileUrl;
    }
}
