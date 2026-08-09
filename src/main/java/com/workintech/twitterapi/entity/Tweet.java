package com.workintech.twitterapi.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "tweet")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Tweet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Tweet içeriği boş olamaz.")
    @Size(max = 280, message = "Tweet en fazla 280 karakter olabilir.")
    @Column(nullable = false, length = 280)
    private String content;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @PrePersist //tweet oluşturulma tarihini tutar createdAt: 2026-08-09 10:40
    public void prePersist() {
        createdAt = LocalDateTime.now();
    }
}