package com.pixelwave.spring_boot.controller;

import com.pixelwave.spring_boot.DTO.Image.ImageMessageDTO;
import com.pixelwave.spring_boot.DTO.chat.ConversationDTO;
import com.pixelwave.spring_boot.DTO.chat.Message;
import com.pixelwave.spring_boot.DTO.post.UploadPostDTO;
import com.pixelwave.spring_boot.model.User;
import com.pixelwave.spring_boot.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/conversation/{conversationId}/messages")
    public ResponseEntity<Page<Message>> getMessagesByConversationId(@PathVariable String conversationId,
                                                                     @RequestParam(defaultValue = "0") int page,
                                                                     @RequestParam(defaultValue = "10") int size) {
        Page<Message> messages = chatService.getMessagesByConversationId(conversationId, page, size);
        return ResponseEntity.ok(messages);
    }

    @PostMapping("/conversation/{conversationId}/image")
    public ResponseEntity<String> sendImageMessage(@PathVariable String conversationId,
                                                   @AuthenticationPrincipal UserDetails userDetails,
                                                   @ModelAttribute ImageMessageDTO imageMessageDTO) {
        chatService.sendImageMessage(conversationId, userDetails,imageMessageDTO);
        return ResponseEntity.ok("Image message sent successfully");
    }
}
