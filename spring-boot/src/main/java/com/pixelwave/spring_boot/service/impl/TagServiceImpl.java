package com.pixelwave.spring_boot.service.impl;

import com.pixelwave.spring_boot.DTO.tag.TagImageResponseDTO;
import com.pixelwave.spring_boot.DTO.tag.TagResponseDTO;
import com.pixelwave.spring_boot.exception.ResourceNotFoundException;
import com.pixelwave.spring_boot.model.Tag;
import com.pixelwave.spring_boot.repository.TagRepository;
import com.pixelwave.spring_boot.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;

    @Autowired
    public TagServiceImpl(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    @Override
    public Tag createTag(String name) {
        if (tagRepository.existsByName(name)) {
            throw new IllegalArgumentException("Tag with name '" + name + "' already exists");
        }
        Tag tag = new Tag();
        tag.setName(name);
        return tagRepository.save(tag);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Tag> getTagById(Long id) {
        return tagRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Tag> getTagByName(String name) {
        return tagRepository.findByName(name);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Tag> getAllTags() {
        return tagRepository.findAll();
    }

    @Override
    public Tag updateTag(Long id, String name) {
        Tag tag = tagRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Tag not found with id: " + id));

        if (!tag.getName().equals(name) && tagRepository.existsByName(name)) {
            throw new IllegalArgumentException("Tag with name '" + name + "' already exists");
        }

        tag.setName(name);
        return tagRepository.save(tag);
    }

    @Override
    public void deleteTag(Long id) {
        if (!tagRepository.existsById(id)) {
            throw new IllegalArgumentException("Tag not found with id: " + id);
        }
        tagRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByName(String name) {
        return tagRepository.existsByName(name);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TagResponseDTO> getTagsSortedByImageCount(int limit) {
        return tagRepository.findAll().stream()
                .map(tag -> TagResponseDTO.builder()
                        .id(tag.getId())
                        .name(tag.getName())
                        .imageCount(tag.getImages().size())
                        .build())
                .sorted((t1, t2) -> Integer.compare(t2.getImageCount(), t1.getImageCount()))
                .limit(limit)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TagImageResponseDTO> getImagesByTagId(Long tagId, Pageable pageable) {
        Tag tag = tagRepository.findById(tagId)
                .orElseThrow(() -> new ResourceNotFoundException("Tag not found with id: " + tagId));

        List<TagImageResponseDTO> images = tag.getImages().stream()
                .map(image -> TagImageResponseDTO.builder()
                        .imageId(image.getId())
                        .url(image.getUrl())
                        .postId(image.getPost() != null ? image.getPost().getId() : null)
                        .likeCount(image.getPost() != null ? image.getPost().getLikeCount() : 0)
                        .build())
                .sorted((i1, i2) -> Integer.compare(i2.getLikeCount(), i1.getLikeCount()))
                .collect(Collectors.toList());

        // Handle empty list
        if (images.isEmpty()) {
            return new PageImpl<>(List.of(), pageable, 0);
        }

        int start = (int) pageable.getOffset();
        
        // Handle invalid start index
        if (start >= images.size()) {
            return new PageImpl<>(List.of(), pageable, images.size());
        }

        int end = Math.min((start + pageable.getPageSize()), images.size());
        
        // Handle case where end is less than start
        if (end <= start) {
            return new PageImpl<>(List.of(), pageable, images.size());
        }

        return new PageImpl<>(
                images.subList(start, end),
                pageable,
                images.size()
        );
    }
} 