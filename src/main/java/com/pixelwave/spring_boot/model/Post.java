package com.pixelwave.spring_boot.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "post")
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "caption")
    private String caption;

    @Column(name = "privacy_setting", length = 20)
    private String privacySetting;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "post_user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<Image> images = new ArrayList<>();

    private int commentCount = 0;
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<Comment> comments = new ArrayList<>();

    private int likeCount = 0;
    @ManyToMany
    @JoinTable(
            name = "post_like",
            joinColumns = @JoinColumn(name = "post_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> likedBy = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "post_tag",
            joinColumns = @JoinColumn(name = "post_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> taggedUsers = new ArrayList<>();

    @ManyToMany(mappedBy = "posts")
    private List<Collection> collections = new ArrayList<>();

//    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
//    private Set<Notification> notifications = new HashSet<>();

    public void setImages(List<Image> images) {
        this.images = images;
        if(images != null) {
            for (Image image : images) {
                image.setPost(this);
            }
        }
    }
    public boolean isLikedByUser(User user) {
        return this.getLikedBy().stream()
                .anyMatch(likedUser -> likedUser.getId().equals(user.getId()));
    }
}

