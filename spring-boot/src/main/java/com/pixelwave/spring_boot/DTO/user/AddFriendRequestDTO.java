package com.pixelwave.spring_boot.DTO.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AddFriendRequestDTO {
    private Long id;

    private UserResponseDTO sender;

    private LocalDateTime createdAt;
}
