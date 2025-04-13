package com.pixelwave.spring_boot.controller;

import com.pixelwave.spring_boot.service.ChannelManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/channels")
public class ChannelController {

    @Autowired
    private ChannelManager channelManager;

    @PostMapping
    public ResponseEntity<Map<String, Object>> createChannel(@RequestParam String channelId) {
        Map<String, Object> response = new HashMap<>();

        boolean created = channelManager.createChannel(channelId);

        response.put("success", created);
        response.put("channelId", channelId);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{channelId}/subscribers")
    public ResponseEntity<Set<String>> getChannelSubscribers(@PathVariable String channelId) {
        Set<String> subscribers = channelManager.getSubscribers(channelId);
        return ResponseEntity.ok(subscribers);
    }

    @DeleteMapping("/{channelId}")
    public ResponseEntity<Map<String, Object>> removeChannel(@PathVariable String channelId) {
        Map<String, Object> response = new HashMap<>();

        boolean removed = channelManager.removeChannel(channelId);

        response.put("success", removed);
        response.put("channelId", channelId);

        return ResponseEntity.ok(response);
    }
}