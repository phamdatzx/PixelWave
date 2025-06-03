package com.pixelwave.spring_boot.service;

import com.pixelwave.spring_boot.DTO.chat.ConversationDTO;
import com.pixelwave.spring_boot.model.Conversation;
import com.pixelwave.spring_boot.model.Message;
import com.pixelwave.spring_boot.model.UserAddFriendRequest;
import com.pixelwave.spring_boot.repository.ConversationRepository;
import com.pixelwave.spring_boot.repository.MessageRepository;
import com.pixelwave.spring_boot.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final JwtService jwtService;
    private final ConversationRepository conservationRepository;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;

    public void createConservation(UserAddFriendRequest userAddFriendRequest) {
        var user1Id = userAddFriendRequest.getSender().getId();
        var user2Id = userAddFriendRequest.getTarget().getId();

        boolean isConservationExists = conservationRepository.existsByUser1IdAndUser2IdOrUser1IdAndUser2Id(user1Id, user2Id, user2Id, user1Id);
        if(!isConservationExists) {
            Conversation conversation = new Conversation();
            conversation.setUser1(userAddFriendRequest.getSender());
            conversation.setUser2(userAddFriendRequest.getTarget());
            conversation.setId(jwtService.generateOpaqueToken());
            conservationRepository.save(conversation);;
        }
    }

    public List<ConversationDTO> getConversationsByUserId(Long id, String search, String sortBy, String sortDirection) {
//        List<Conservation> conversations = conservationRepository.findByUserId(id, search, sortBy, sortDirection);
//        return conversations.stream()
//                .map(conversation -> new ConversationDTO(
//                        conversation.getId(),
//                        conversation.getUser1().getId(),
//                        conversation.getUser2().getId(),
//                        conversation.getLastMessage(),
//                        conversation.getLastUpdated()))
//                .toList();
        return null;
    }

    public Message createMessage(String conversationId, String content, Long senderId) {

        var sender = userRepository.findById(senderId)
                .orElseThrow(() -> new IllegalArgumentException("Sender not found"));

        var conservation = conservationRepository.findById(conversationId)
                .orElseThrow(() -> new IllegalArgumentException("Conversation not found"));


        Message message = Message.builder()
                .content(content)
                .sender(sender)
                .conversation(conservation)
                .createdAt(LocalDateTime.now())
                .build();


        return messageRepository.save(message); // Placeholder return value
    }
}
