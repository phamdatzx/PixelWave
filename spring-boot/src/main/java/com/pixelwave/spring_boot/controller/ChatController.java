package com.pixelwave.spring_boot.controller;

import com.pixelwave.spring_boot.DTO.chat.ConversationDTO;
import com.pixelwave.spring_boot.model.User;
import com.pixelwave.spring_boot.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @GetMapping("/conversations")
    public ResponseEntity<List<ConversationDTO>> getConversations(@AuthenticationPrincipal UserDetails userDetails,
                                                                  @RequestParam(required = false) String search,
                                                                  @RequestParam(defaultValue = "lastUpdated") String sortBy,
                                                                  @RequestParam(defaultValue = "desc") String sortDirection) {
        return ResponseEntity.ok(
                chatService.getConversationsByUserId(
                        ((User)userDetails).getId(),
                    search,
                    sortBy,
                    sortDirection)
        );
    }
}
