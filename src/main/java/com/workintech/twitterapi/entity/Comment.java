package com.workintech.twitterapi.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "comment")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Yorum boş olamaz.")
    @Size(max = 280, message = "Yorum en fazla 280 karakter olabilir.")
    @Column(nullable = false, length = 280)
    private String content;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /*User  1 ---- N Comment
      Tweet 1 ---- N Comment*/

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tweet_id", nullable = false)
    private Tweet tweet;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
    }
}