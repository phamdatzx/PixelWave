package com.pixelwave.spring_boot.service;

import com.pixelwave.spring_boot.model.Conversation;
import com.pixelwave.spring_boot.repository.ConversationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChannelService {

    private ConversationRepository conservationRepository;

    public boolean canUserAccessChannel(String userId, String channelId) {
        return false;
    }
}