package com.pixelwave.spring_boot.service;

import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ChannelManager {

    // Store channels and their subscribers
    private final Map<String, Set<String>> channels = new ConcurrentHashMap<>();

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
     * @return true if subscription was successful
     */
    public boolean subscribe(String channelId, String userId) {
        if (!channels.containsKey(channelId)) {
            createChannel(channelId);
        }
        return channels.get(channelId).add(userId);
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
