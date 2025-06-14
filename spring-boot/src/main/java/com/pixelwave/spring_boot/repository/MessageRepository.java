package com.pixelwave.spring_boot.repository;

import com.pixelwave.spring_boot.model.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MessageRepository extends JpaRepository<Message, Long> {
    // Custom query methods can be defined here if needed
    // For example, to find messages by sender and receiver:
    // List<Message> findBySenderAndReceiver(User sender, User receiver);

    Page<Message> findByConversationId(String conversationId, Pageable pageable);

    Optional<Message> findTopByConversationIdOrderByCreatedAtDesc(String id);
}
