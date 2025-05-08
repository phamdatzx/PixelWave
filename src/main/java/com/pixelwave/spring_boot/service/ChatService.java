package com.pixelwave.spring_boot.service;

import com.pixelwave.spring_boot.model.Conservation;
import com.pixelwave.spring_boot.model.UserAddFriendRequest;
import com.pixelwave.spring_boot.repository.ConservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final JwtService jwtService;
    private final ConservationRepository conservationRepository;

    public void createConservation(UserAddFriendRequest userAddFriendRequest) {
        var user1Id = userAddFriendRequest.getSender().getId();
        var user2Id = userAddFriendRequest.getTarget().getId();

        boolean isConservationExists = conservationRepository.existsByUser1IdAndUser2IdOrUser1IdAndUser2Id(user1Id, user2Id, user2Id, user1Id);
        if(!isConservationExists) {
            Conservation conservation = new Conservation();
            conservation.setUser1(userAddFriendRequest.getSender());
            conservation.setUser2(userAddFriendRequest.getTarget());
            conservation.setId(jwtService.generateOpaqueToken());
            conservationRepository.save(conservation);;
        }
    }
}
