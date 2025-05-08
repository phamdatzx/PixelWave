package com.pixelwave.spring_boot.repository;

import com.pixelwave.spring_boot.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Long> {
    // Custom query methods can be defined here if needed
    // For example, to find messages by sender and receiver:
    // List<Message> findBySenderAndReceiver(User sender, User receiver);
}
