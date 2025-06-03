package com.pixelwave.spring_boot.service;

import com.pixelwave.spring_boot.model.Conversation;
import com.pixelwave.spring_boot.repository.ConversationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ConversationService {
    private final ConversationRepository conversationRepository;

    public Conversation getConversationById(String id) {
        return conversationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Conversation not found: " + id));
    }

    public boolean isUserIdInConversation(Long userId, String conversationId) {
        Conversation conversation = getConversationById(conversationId);
        return conversation.getUser1().getId().equals(userId) || conversation.getUser2().getId().equals(userId);
    }


}
