package com.pixelwave.spring_boot.service;

import com.pixelwave.spring_boot.model.Tag;
import java.util.List;
import java.util.Optional;

public interface TagService {
    Tag createTag(String name);
    Optional<Tag> getTagById(Long id);
    Optional<Tag> getTagByName(String name);
    List<Tag> getAllTags();
    Tag updateTag(Long id, String name);
    void deleteTag(Long id);
    boolean existsByName(String name);
} 