package com.pixelwave.spring_boot.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class Violate {
    @Id
    private String id;

    @ManyToOne
    private  User user;

    private String reason;

    private LocalDateTime createdAt;

}
