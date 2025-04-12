package com.pixelwave.spring_boot.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_friends_request")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
public class UserAddFriendRequest {

    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sender_user_id", nullable = false)
    private User sender;

    @ManyToOne
    @JoinColumn(name = "target_user_id", nullable = false)
    private User target;

    private String status; // "PENDING", "ACCEPTED", "REJECTED"

    private LocalDateTime createdAt;
}
