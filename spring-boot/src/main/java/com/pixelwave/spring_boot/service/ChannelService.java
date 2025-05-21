package com.pixelwave.spring_boot.service;

import com.pixelwave.spring_boot.exception.ResourceNotFoundException;
import com.pixelwave.spring_boot.model.Conservation;
import com.pixelwave.spring_boot.repository.ConservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChannelService {

    private ConservationRepository conservationRepository;

    public boolean canUserAccessChannel(String userId, String channelId) {
        return false;
    }
}