package com.pixelwave.spring_boot.model;

import jakarta.persistence.*;
import lombok.*;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "image")
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "size")
    private Long size;

    @Column(name = "url", nullable = false)
    private String url;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    private Post post;

    @ManyToOne
    private Comment comment;

    @ManyToOne
    @JoinColumn(name = "message_id")
    private Message message;

}
