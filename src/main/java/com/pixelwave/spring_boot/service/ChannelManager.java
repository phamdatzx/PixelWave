package com.pixelwave.spring_boot.service;

import com.pixelwave.spring_boot.model.Conservation;
import com.pixelwave.spring_boot.repository.ConservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class ChannelManager {

    // Store channels and their subscribers
    private final Map<String, Set<String>> channels = new ConcurrentHashMap<>();

    private final ConservationRepository conservationRepository;

    /**
     * Create a new channel
     * @param channelId The channel identifier
     * @return true if channel was created, false if it already exists
     */
    public boolean createChannel(String channelId) {
        if (channels.containsKey(channelId)) {
            return false;
        }
        channels.put(channelId, ConcurrentHashMap.newKeySet());
        return true;
    }

    /**
     * Subscribe a user to a channel
     * @param channelId The channel identifier
     * @param userId The user identifier
     */
    public void subscribe(String channelId, String userId) {
        if (!channels.containsKey(channelId)) {
            createChannel(channelId);
        }

        // Check if the channel exists in the database
        Conservation conservation = conservationRepository.findById(channelId)
                .orElseThrow(() -> new IllegalArgumentException("Channel not found: " + channelId));

        // Check if the user is in the conservation
        if(conservation.getUser1().getId().equals(userId) || conservation.getUser2().getId().equals(userId)) {
            channels.get(channelId).add(userId);
        }
    }

    /**
     * Unsubscribe a user from a channel
     * @param channelId The channel identifier
     * @param userId The user identifier
     * @return true if unsubscription was successful
     */
    public boolean unsubscribe(String channelId, String userId) {
        if (!channels.containsKey(channelId)) {
            return false;
        }
        return channels.get(channelId).remove(userId);
    }

    /**
     * Check if a channel exists
     * @param channelId The channel identifier
     * @return true if channel exists
     */
    public boolean channelExists(String channelId) {
        return channels.containsKey(channelId);
    }

    /**
     * Get subscribers of a channel
     * @param channelId The channel identifier
     * @return Set of subscribers or empty set if channel doesn't exist
     */
    public Set<String> getSubscribers(String channelId) {
        return channels.getOrDefault(channelId, ConcurrentHashMap.newKeySet());
    }

    /**
     * Remove a channel
     * @param channelId The channel identifier
     * @return true if channel was removed
     */
    public boolean removeChannel(String channelId) {
        return channels.remove(channelId) != null;
    }
}
