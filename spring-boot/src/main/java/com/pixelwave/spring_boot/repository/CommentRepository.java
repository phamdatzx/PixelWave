package com.pixelwave.spring_boot.repository;

import com.pixelwave.spring_boot.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {

}
