package com.pixelwave.spring_boot.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Message {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conservation_id", nullable = false)
    private Conservation conservation;

    private String content;

    @ManyToOne
    private User receiver;

    LocalDateTime createdAt;

    @OneToMany(mappedBy = "message", cascade = CascadeType.ALL)
    private List<Image> images;
}
