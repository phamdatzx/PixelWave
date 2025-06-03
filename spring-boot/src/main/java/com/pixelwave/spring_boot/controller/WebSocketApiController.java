package com.pixelwave.spring_boot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.pixelwave.spring_boot.service.WebSocketService;

@RestController
@RequestMapping("/api/websocket")
public class WebSocketApiController {

    @Autowired
    private WebSocketService webSocketService;

    @PostMapping("/send/{userId}")
    public ResponseEntity<String> sendMessageToUser(
            @PathVariable String userId,
            @RequestParam String message) {

        boolean sent = webSocketService.sendNotificationToUser(userId, message);

        if (sent) {
            return ResponseEntity.ok("Message sent to user: " + userId);
        } else {
            return ResponseEntity.badRequest().body("User not connected: " + userId);
        }
    }

    @PostMapping("/send/session/{userId}")
    public ResponseEntity<String> sendMessageToUserbySession(
            @PathVariable String userId,
            @RequestParam String message) {

        boolean sent = webSocketService.sendNotificationToUserBySession(userId, message);

        if (sent) {
            return ResponseEntity.ok("Message sent to user: " + userId);
        } else {
            return ResponseEntity.badRequest().body("User not connected: " + userId);
        }
    }

    @GetMapping("/status/{userId}")
    public ResponseEntity<Boolean> getUserStatus(@PathVariable String userId) {
        boolean isOnline = webSocketService.isUserOnline(userId);
        return ResponseEntity.ok(isOnline);
    }

    @PostMapping("/broadcast")
    public ResponseEntity<String> broadcastMessage(@RequestParam String message) {
        //webSocketService.sendToTopic("general", message);
        return ResponseEntity.ok("Message broadcasted");
    }
}