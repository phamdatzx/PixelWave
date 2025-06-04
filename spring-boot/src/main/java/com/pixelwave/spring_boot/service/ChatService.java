package com.pixelwave.spring_boot.service;

import com.pixelwave.spring_boot.DTO.chat.ConversationDTO;
import com.pixelwave.spring_boot.DTO.user.UserDTO;
import com.pixelwave.spring_boot.model.Conversation;
import com.pixelwave.spring_boot.model.Message;
import com.pixelwave.spring_boot.model.UserAddFriendRequest;
import com.pixelwave.spring_boot.repository.ConversationRepository;
import com.pixelwave.spring_boot.repository.MessageRepository;
import com.pixelwave.spring_boot.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final JwtService jwtService;
    private final ConversationRepository conversationRepository;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;

    public void createConservation(UserAddFriendRequest userAddFriendRequest) {
        var user1Id = userAddFriendRequest.getSender().getId();
        var user2Id = userAddFriendRequest.getTarget().getId();

        boolean isConservationExists = conversationRepository.existsByUser1IdAndUser2IdOrUser1IdAndUser2Id(user1Id, user2Id, user2Id, user1Id);
        if(!isConservationExists) {
            Conversation conversation = new Conversation();
            conversation.setUser1(userAddFriendRequest.getSender());
            conversation.setUser2(userAddFriendRequest.getTarget());
            conversation.setId(jwtService.generateOpaqueToken());
            conversationRepository.save(conversation);;
        }
    }

    public List<ConversationDTO> getConversationsByUserId(Long id, String search, String sortBy, String sortDirection) {
        // Set default values if not provided
        String searchTerm = search != null ? search.toLowerCase() : "";
        String sortField = sortBy != null ? sortBy : "lastUpdated"; // Default sort field
        Sort.Direction direction = "desc".equalsIgnoreCase(sortDirection) ? Sort.Direction.DESC : Sort.Direction.ASC;

        // Create pageable with sorting
        Sort sort = Sort.by(direction, sortField);
        Pageable pageable = PageRequest.of(0, 100, sort); // Using a high limit to get all conversations

        // Get conversations where the user is either user1 or user2
        List<Conversation> conversations = conversationRepository.findByUserIdWithSearch(id, id, searchTerm, pageable);

        // Map to DTOs
        return conversations.stream()
                .map(conversation -> {
                    // Determine the other user in the conversation
                    boolean isUser1 = conversation.getUser1().getId().equals(id);
                    var otherUser = isUser1 ? conversation.getUser2() : conversation.getUser1();

                    return ConversationDTO.builder()
                            .id(conversation.getId())
                            .user(UserDTO.builder()
                                    .id(otherUser.getId())
                                    .fullName(otherUser.getFullName())
                                    .avatar(otherUser.getAvatar())
                                    .build())
                            .lastUpdated(conversation.getLastUpdated())
                            .build();
                })
                .collect(Collectors.toList());
    }

    public Message createMessage(String conversationId, String content, Long senderId) {

        var sender = userRepository.findById(senderId)
                .orElseThrow(() -> new IllegalArgumentException("Sender not found"));

        var conservation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new IllegalArgumentException("Conversation not found"));


        Message message = Message.builder()
                .content(content)
                .sender(sender)
                .conversation(conservation)
                .createdAt(LocalDateTime.now())
                .build();


        return messageRepository.save(message); // Placeholder return value
    }

    public Page<com.pixelwave.spring_boot.DTO.chat.Message> getMessagesByConversationId(String conversationId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Message> messagesPage = messageRepository.findByConversationId(conversationId, pageable);

        return messagesPage.map(message -> com.pixelwave.spring_boot.DTO.chat.Message.builder()
                .id(message.getId())
                .content(message.getContent())
                .sender(UserDTO.builder()
                        .id(message.getSender().getId())
                        .fullName(message.getSender().getFullName())
                        .avatar(message.getSender().getAvatar())
                        .build())
                .createdAt(message.getCreatedAt())
                .images(message.getImages().stream()
                        .map(image -> new com.pixelwave.spring_boot.DTO.Image.ImageDTO(image.getId(),image.getSize(), image.getUrl()))
                        .collect(Collectors.toList()))
                .build());
    }
}
